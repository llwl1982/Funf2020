package com.asiainfo.funf2020.presenter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.system.AppUtils;
import com.asiainfo.funf2020.R;
import com.asiainfo.funf2020.contract.MeetingModeContract;
import com.asiainfo.funf2020.data.meetingmode.bean.FinishUploadResponse;
import com.asiainfo.funf2020.data.meetingmode.bean.RequestUploadResponse;
import com.asiainfo.funf2020.data.meetingmode.biz.IMeetingModeBiz;
import com.asiainfo.funf2020.data.meetingmode.biz.MeetingModeBiz;
import com.asiainfo.funf2020.mvp.MVPPresenter;
import com.asiainfo.funf2020.service.AudioFeatureService;
import com.asiainfo.funf2020.utils.net.JsonHttpResponseHandler;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import edu.mit.media.funf.util.LogUtil;

/**
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public class MeetingModePresenter extends MVPPresenter<MeetingModeContract.View> implements
		MeetingModeContract.Presenter {

	private final static String TAG = MeetingModePresenter.class.getSimpleName();

	/**
	 * 音频采集服务
	 */
	private AudioFeatureService mAudioFeatureService;
	private Intent intent;
	private Context context;

	// 持有模型的引用
	private IMeetingModeBiz mIMeetingModeBiz;

	/**
	 * 全局变量，用于判断是调用requestUpload方法还是调用finishUpload方法
	 * 只有调用requestUpload方法返回之后才能改变这个flag为false
	 * 为true永远调用requestUpload方法
	 */
	private boolean recordingFlag = true;

	/**
	 * 构造方法
	 */
	public MeetingModePresenter(Context context) {
		this.intent = new Intent(context, AudioFeatureService.class);
		this.context = context;
		// 初始化模型
		mIMeetingModeBiz = new MeetingModeBiz(context);
		// 初始化的时候如果录音服务时启动的，那么就停止它
		// 用于停止上个界面启动的录音服务
		if (isRecordingRunning(context))
			stopRecord(context);
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
	 * 判断音频服务是否运行
	 */
	private boolean isRecordingRunning(Context context) {
		return AppUtils.isServiceRunning(context, AudioFeatureService.class.getName());
	}

	private void startRecord(Context context) {
		context.startService(intent);
		context.bindService(intent, audioServiceConn, Context.BIND_AUTO_CREATE);

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

	}

	@Override
	public void doRecording() {
		if (recordingFlag) {

			// 启动录音服务
//			startRecord(context);
			// 将标志位修改为false
			recordingFlag = false;
			MeetingModeContract.View view = getView();
			if (view != null && !view.isAtBack()) {
				// 屏幕闪烁
				view.setSplashOn();
				view.setDoubleTapText(context.getString(R.string.double_tap_screen_stop));
			}

		} else {

//			stopRecord(context);
			recordingFlag = true;
			MeetingModeContract.View view = getView();
			if (view != null && !view.isAtBack()) {
				// 屏幕闪烁
				view.setSplashOff();
				view.setDoubleTapText(context.getString(R.string.double_tap_screen_start));
			}
		}
	}


//	@Override
//	public void doRecording() {
//		//TODO:发送网络请求判断是否能开启录音，如果能才开启
//
//		if (recordingFlag) {
//			// 调用查询接口判断是否开启
//			LogUtil.i(TAG, "调用requestUpload方法");
//			mIMeetingModeBiz.requestUpload(new JsonHttpResponseHandler<RequestUploadResponse>() {
//				@Override
//				public void onHandleSuccess(int statusCode, Header[] headers,
//				                            RequestUploadResponse jsonObj) {
//					// 返回true表明可以上传
//					if (jsonObj.data.result) {
//						// 启动录音服务
//						startRecord(context);
//						// 将标志位修改为false
//						recordingFlag = false;
//						MeetingModeContract.View view = getView();
//						if (view != null && !view.isAtBack()) {
//							// 屏幕闪烁
//							view.setSplashOn();
//							view.setDoubleTapText(context.getString(R.string.double_tap_screen_stop));
//						}
//					}
//				}
//
//				@Override
//				public void onCancel() {
//					// 没有网络的情况会终止请求
//					ToastUtils.getInstance().showToast(context,
//							R.string.prompt_request_upload_failure);
//				}
//
//				@Override
//				public void onHandleFailure(String errorMsg) {
//					ToastUtils.getInstance().showToast(context,
//							R.string.prompt_request_upload_failure);
//				}
//			});
//		} else {
//			LogUtil.i(TAG, "调用finishUpload方法");
//			mIMeetingModeBiz.finishUpload(new JsonHttpResponseHandler<FinishUploadResponse>() {
//				@Override
//				public void onHandleSuccess(int statusCode, Header[] headers,
//				                            FinishUploadResponse jsonObj) {
//					// 返回true表明可以上传
//					if (jsonObj.data.result.equals("SUCCESS")) {
//						// 启动录音服务
//						stopRecord(context);
//						recordingFlag = true;
//						MeetingModeContract.View view = getView();
//						if (view != null && !view.isAtBack()) {
//							// 屏幕闪烁
//							view.setSplashOff();
//							view.setDoubleTapText(context.getString(R.string.double_tap_screen_start));
//						}
//
//					}
//				}
//
//				@Override
//				public void onCancel() {
//					// 没有网络的情况会终止请求
//					ToastUtils.getInstance().showToast(context,
//							R.string.prompt_request_upload_failure);
//				}
//
//				@Override
//				public void onHandleFailure(String errorMsg) {
//					ToastUtils.getInstance().showToast(context,
//							R.string.prompt_request_upload_failure);
//				}
//			});
//
//		}
//
//	}

	@Override
	public void listenAudioRecording() {
		// TODO:通过监听录音状态来控制屏幕闪烁
		final int MSG_RUNNING = 0x111;
		final int MSG_STOP = 0x222;
		@SuppressLint("HandlerLeak")
		final Handler checkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				MeetingModeContract.View view = getView();
				switch (msg.what) {
					case MSG_RUNNING:
						if (view != null)
							view.onListenRecording(true);
						break;
					case MSG_STOP:
						if (view != null)
							view.onListenRecording(false);
						break;


				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 界面在前台时循环监听服务状态,并更新界面
				MeetingModeContract.View view = getView();
				while (view != null && !view.isAtBack()) {
					// 10秒闪烁一次
					try {
						TimeUnit.SECONDS.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					checkHandler.obtainMessage(checkAudioRecording() ? MSG_RUNNING : MSG_STOP)
							.sendToTarget();
				}
			}
		}).start();

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
}
