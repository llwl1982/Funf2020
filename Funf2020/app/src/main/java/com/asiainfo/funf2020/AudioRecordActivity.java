package com.asiainfo.funf2020;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;

import com.ai2020lab.aiutils.system.AppUtils;
import com.ai2020lab.aiutils.thread.TaskSimpleRunnable;
import com.ai2020lab.aiutils.thread.ThreadUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;


public class AudioRecordActivity extends AppCompatActivity {

	private final static String TAG = AudioRecordActivity.class.getSimpleName();

	public static final int MSG_RUNNING = 0x11111;
	public static final int MSG_STOP = 0x22222;

	private Intent intent;
	Button scanNowButton;

	private AudioFeatureService mAudioFeatureService;

	CheckHandler mCheckHandler;

	private boolean isAtBack = false;


	/**
	 * 同AudioFeatureSerive需要将数据返回给Activity
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioRecordActivity", "--重新创建AudioRecordActivity");
		setContentView(R.layout.activity_audio_record);
		scanNowButton = (Button) findViewById(R.id.scanNowButton);
		intent = new Intent(this, AudioFeatureService.class);
		mCheckHandler = new CheckHandler(this);

		scanNowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppUtils.isServiceRunning(AudioRecordActivity.this,
						AudioFeatureService.class.getName())) {
					stopRecord();
				} else {
					startRecord();
				}

			}
		});
	}

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

	@Override
	protected void onStart() {
		super.onStart();
		scanNowButton.setVisibility(View.GONE);
		isAtBack = false;
		// 检测到service已经在运行则同Service建立连接
		if (AppUtils.isServiceRunning(this, AudioFeatureService.class.getName())) {
			Log.i(TAG, "--onStart 中绑定 AudioFeatureService");
			bindService(intent, audioServiceConn, BIND_AUTO_CREATE);
		}
		// 用户进入界面开始监听服务状态
		listenAudioRecording();
	}

	/**
	 * 设置按钮文字
	 */
	private void setScanNowButton(boolean isRunning) {
		String stop = getString(R.string.stop);
		String record = getString(R.string.record);
		if (isRunning && !scanNowButton.getText().toString().equals(stop)) {
			Log.i(TAG, "--设置采集按钮为停止--");
			scanNowButton.setText(stop);
		} else if (!isRunning && !scanNowButton.getText().toString().equals(record)) {
			Log.i(TAG, "--设置采集按钮为开始--");
			scanNowButton.setText(getString(R.string.record));
		}
		scanNowButton.setVisibility(View.VISIBLE);
	}

	/**
	 * 更新界面
	 */
	static class CheckHandler extends Handler {

		private final WeakReference<AudioRecordActivity> mActivity;

		public CheckHandler(AudioRecordActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AudioRecordActivity activity = mActivity.get();
			switch (msg.what) {
				case MSG_RUNNING:
					activity.setScanNowButton(true);
					break;
				case MSG_STOP:
					activity.setScanNowButton(false);
					break;
			}
		}
	}


	/**
	 * 每秒都对录音服务的状态进行监测,并更新界面
	 */
	private void listenAudioRecording() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 界面在前台时循环监听服务状态,并更新界面
				while (!isAtBack) {
					mCheckHandler.obtainMessage(checkAudioRecording() ? MSG_RUNNING :
							MSG_STOP).sendToTarget();
					try {
						// 一秒钟更新一次界面
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	private void startRecord() {
		startService(intent);
		scanNowButton.setText(getString(R.string.stop));
		bindService(intent, audioServiceConn, BIND_AUTO_CREATE);
	}

	private void stopRecord() {
		// 因为是先调用的startService再调用的bindService启动的服务，
		// 所以停止服务时，先调用unBindService再调用stopService
		unBinAudioFeatureService();
		stopService(intent);
		scanNowButton.setText(getString(R.string.record));
	}

	private void unBinAudioFeatureService() {
		try {
			unbindService(audioServiceConn);
		} catch (Exception e) {
			Log.e(TAG, "--解除绑定异常", e);
		}
		mAudioFeatureService = null;
	}

	@Override
	protected void onStop() {
		super.onStop();
		scanNowButton.setVisibility(View.GONE);
		isAtBack = true;
		Log.i(TAG, "--AudioRecordActivity回到后台");
		// 界面回到后台，如果服务还在则同服务解除绑定
		if (checkAudioRecording()) {
			Log.i(TAG, "--onStop 中解除同 AudioFeatureService  绑定");
			unBinAudioFeatureService();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "--AudioRecordActivity挂了");
		// 解除与Service的绑定
		if (checkAudioRecording()) {
			Log.i(TAG, "--onDestroy 中解除同 AudioFeatureService  绑定");
			unBinAudioFeatureService();
		}
	}
}
