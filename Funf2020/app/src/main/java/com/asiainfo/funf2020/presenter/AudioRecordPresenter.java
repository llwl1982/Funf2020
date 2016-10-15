package com.asiainfo.funf2020.presenter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ai2020lab.aiutils.common.StringUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.storage.StorageUtils;
import com.ai2020lab.aiutils.system.AppUtils;
import com.asiainfo.funf2020.AudioFeatureService;
import com.asiainfo.funf2020.R;
import com.asiainfo.funf2020.contract.AudioRecordContract;
import com.asiainfo.funf2020.mvp.MVPModel;
import com.asiainfo.funf2020.mvp.MVPPresenter;
import com.asiainfo.funf2020.view.AudioRecordView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import edu.mit.media.funf.util.LogUtil;

/**
 * Created by Justin Z on 2016/10/12.
 * 502953057@qq.com
 */
public class AudioRecordPresenter extends MVPPresenter<AudioRecordContract.View> implements
		AudioRecordContract.Presenter {

	private final static String TAG = AudioRecordPresenter.class.getSimpleName();

	// Presenter持有Model和View的引用

	/**
	 * 音频采集服务
	 */
	private AudioFeatureService mAudioFeatureService;
	private Intent intent;
	private Context context;

	/**
	 * 构造方法
	 */
	public AudioRecordPresenter(Context context) {
		this.intent = new Intent(context, AudioFeatureService.class);
		this.context = context;
	}

	/**
	 * 同AudioFeatureService需要将数据返回给Activity
	 */
	private ServiceConnection audioServiceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "--Activity同AudioFeatureService建立连接");
			mAudioFeatureService = ((AudioFeatureService.AudioFeatureBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "--Activity同AudioFeatureService断开连接");
		}
	};

	/**
	 * 界面上检查音频采集服务是否已经绑定，用于更新按钮状态
	 */
	private boolean checkAudioRecording() {
		if (mAudioFeatureService != null && mAudioFeatureService.getStatus()) {
			Log.i(TAG, "--音频采集服务绑定--");
			return true;
		} else {
			Log.i(TAG, "--音频采集服务未绑定--");
			return false;
		}
	}

	/**
	 * 监听录制状态
	 */
	@Override
	public void listenAudioRecording() {
		final int MSG_RUNNING = 0x111;
		final int MSG_STOP = 0x222;
		@SuppressLint("HandlerLeak")
		final Handler checkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_RUNNING:
						getView().onListenRecording(true);
						break;
					case MSG_STOP:
						getView().onListenRecording(false);
						break;

				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 界面在前台时循环监听服务状态,并更新界面
				while (!getView().isAtBack()) {
					// 休眠一秒
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					checkHandler.obtainMessage(checkAudioRecording() ? MSG_RUNNING : MSG_STOP)
							.sendToTarget();
				}
			}
		}).start();

	}

	/**
	 * 4舍5入并保留digits位小数
	 */
	private float roundFloat(float num, int digits) {
		return StringUtils.parseFloat(String.format("%." + "" + digits + "f", num));
	}

	/**
	 * 计算缓存大小
	 */
	private float calculateCache(Context context) {
		String path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName()).getPath() + File.separator +
				"default";
		LogUtil.i(TAG, "缓存路径-->" + path);
		long totalSize = StorageUtils.getInstance(context).getTotalSize(path);
		long availableSize = StorageUtils.getInstance(context).getAvailableSize(path);
		long usedSize = totalSize - availableSize;
		float usedSizeFloat = roundFloat((float) usedSize / StorageUtils.VolumeConvert.MB_2_BYTE, 2);
		LogUtil.i(TAG, "已用容量-->" + usedSizeFloat);
		return usedSizeFloat;
	}

	/**
	 * 监听内存容量
	 */
	@Override
	public void listenSDCard() {

		final int MSG_CALCULATE = 0x333;

		@SuppressLint("HandlerLeak")
		final Handler checkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_CALCULATE:
						String text = String.format(context.getString(R.string.clear_cache1),
								msg.obj + "MB");
						getView().setSDCardButtonText(text);
						break;

				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 界面在前台时循环监听服务状态,并更新界面
				while (!getView().isAtBack()) {

					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					checkHandler.obtainMessage(MSG_CALCULATE, calculateCache(context))
							.sendToTarget();
				}
			}
		}).start();

	}

	/**
	 * 判断音频服务是否运行
	 */
	private boolean isRecordingRunning(Context context) {
		return AppUtils.isServiceRunning(context, AudioFeatureService.class.getName());
	}

	private void startRecord(Context context) {
		context.startService(intent);
		context.bindService(intent, audioServiceConn, Context.BIND_AUTO_CREATE);
		getView().setScanNowButtonText(context.getString(R.string.stop));
	}

	private void stopRecord(Context context) {
		// 因为是先调用的startService再调用的bindService启动的服务，
		// 所以停止服务时，先调用unBindService再调用stopService
		try {
			context.unbindService(audioServiceConn);
		} catch (Exception e) {
			Log.e(TAG, "--解除绑定异常", e);
		}
		mAudioFeatureService = null;
		context.stopService(intent);
		getView().setScanNowButtonText(context.getString(R.string.record));
	}

	/**
	 * 录制或停止录制的实现
	 */
	@Override
	public void doRecording() {
		if (isRecordingRunning(context)) {
			stopRecord(context);
		} else {
			startRecord(context);
		}
	}

	@Override
	public void bindRecordingService() {
		if (isRecordingRunning(context))
			context.bindService(intent, audioServiceConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void unBindRecordingService() {
		if (!isRecordingRunning(context)) return;
		try {
			context.unbindService(audioServiceConn);
		} catch (Exception e) {
			Log.e(TAG, "--解除绑定异常", e);
		}
		mAudioFeatureService = null;
	}

	@Override
	public void delCache() {
		String path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName()).getPath() + File.separator +
				"default";
		FileUtils.deleteDirectory(path);
	}
}
