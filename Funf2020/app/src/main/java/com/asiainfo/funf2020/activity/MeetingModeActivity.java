package com.asiainfo.funf2020.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;

import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.asiainfo.funf2020.R;
import com.asiainfo.funf2020.contract.MeetingModeContract;
import com.asiainfo.funf2020.mvp.MVPActivity;
import com.asiainfo.funf2020.presenter.MeetingModePresenter;
import com.asiainfo.funf2020.utils.CommonUtils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 会议模式操作界面
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public class MeetingModeActivity extends MVPActivity<MeetingModeContract.View,
		MeetingModePresenter> implements MeetingModeContract.View {

	private final static String TAG = MeetingModeActivity.class.getSimpleName();

	private final static int SOS_LIMIT = 21;

	private RelativeLayout meetingModeRl;
	private TextView doubleTapTv;

	private GestureDetector detector;

	private boolean isAtBack = false;

	private Timer timer = null;
	private TimerTask sosTask = null;

	private Activity activity;

	private boolean sosFlag = false;
	private boolean splashFlag = false;
	private int sosCount = 0;

	/**
	 * 初始化界面
	 */
	private void initViews() {
		meetingModeRl = (RelativeLayout) findViewById(R.id.meeting_mode_rl);
		doubleTapTv = (TextView) findViewById(R.id.double_tap_tv);
	}

	@Override
	public MeetingModePresenter initPresenter() {
		return new MeetingModePresenter(this);
	}

	/**
	 * 监听录音动作
	 */
	@Override
	public void onListenRecording(boolean isRunning) {
		if (!isRunning && splashFlag) {
			// 关闭闪烁
			setSplashOff();
			setDoubleTapText(getString(R.string.double_tap_screen_start));
		} else if (isRunning && !splashFlag) {
			setSplashOn();
			setDoubleTapText(getString(R.string.double_tap_screen_stop));
		}
	}

	/**
	 * 控制屏幕闪烁
	 */
	@Override
	public void setSplashOn() {
		LogUtils.i(TAG, "--闪光灯开启--");
		final int MSG_ON = 0x555;
		final int MSG_OFF = 0x666;

		@SuppressLint("HandlerLeak")
		final Handler sosHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_ON:
						if (sosCount == SOS_LIMIT) {
							setSplashOff();
							return;
						}
//						DisplayUtils.setScreenBrightness(activity, 1.0f);
//						CommonUtils.setSplashLightOn(activity);
						meetingModeRl.setBackgroundColor(
								ResourcesUtils.getColor(android.R.color.white));
						setDoubleTapTextColor(ResourcesUtils.getColor(android.R.color.black));
						sosFlag = true;
						sosCount++;

						break;
					case MSG_OFF:
//						DisplayUtils.setScreenBrightness(activity, 0f);
//						CommonUtils.setSplashLightOff();
						meetingModeRl.setBackgroundColor(
								ResourcesUtils.getColor(android.R.color.black));
						setDoubleTapTextColor(ResourcesUtils.getColor(android.R.color.white));
						sosFlag = false;
						break;
				}
			}
		};
		timer = new Timer();
		sosTask = new TimerTask() {
			@Override
			public void run() {
				if (!sosFlag) {
					LogUtils.i(TAG, "--SOS开--");
					sosHandler.obtainMessage(MSG_ON).sendToTarget();
				} else {
					LogUtils.i(TAG, "--SOS关--");
					sosHandler.obtainMessage(MSG_OFF).sendToTarget();
				}
			}
		};
		timer.schedule(sosTask, 0, 200);
		splashFlag = true;

	}

	@Override
	public void setSplashOff() {
		if (timer != null && sosTask != null) {
			sosTask.cancel();
			timer.cancel();
		}
		sosTask = null;
		timer = null;
		sosCount = 0;
		splashFlag = false;

		// TODO:这里需要延迟设置，否者可能不生效
		ThreadUtils.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				meetingModeRl.setBackgroundColor(
						ResourcesUtils.getColor(android.R.color.black));
//				DisplayUtils.setScreenBrightness(activity, 1.0f);
				setDoubleTapTextColor(ResourcesUtils.getColor(android.R.color.white));
				sosFlag = false;
//				CommonUtils.setSplashLightOff();
			}
		}, 200);

	}

	@Override
	public void setDoubleTapText(String text) {
		doubleTapTv.setText(text);
	}

	@Override
	public void setDoubleTapTextColor(int color){
		doubleTapTv.setTextColor(color);
	}


	@Override
	public boolean isAtBack() {
		LogUtils.i(TAG, "--判断 MeetingModeActivity 是否在后台--");
		return isAtBack;
	}

	@Override
	protected void onStart() {
		super.onStart();
		isAtBack = false;
		// 检测到service已经在运行则同Service建立连接
		getPresenter().bindRecordingService();
		// 用户进入界面开始监听服务状态
		getPresenter().listenAudioRecording();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		Log.i("MeetingModeActivity", "--MeetingModeActivity");
		setContentView(R.layout.activity_meeting_mode);
		// 保持屏幕高亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initViews();
		detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			// 监听双击事件
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				//TODO:处理双击手势
				getPresenter().doRecording();
				return super.onDoubleTap(e);
			}


		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		isAtBack = true;
		Log.i(TAG, "--AudioRecordActivity回到后台");
		// 界面回到后台，如果服务还在则同服务解除绑定
		getPresenter().unBindRecordingService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "--AudioRecordActivity挂了");
		// 解除与Service的绑定
		getPresenter().unBindRecordingService();
		sosFlag = false;
		sosCount = 0;
		splashFlag = false;
	}


	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return detector.onTouchEvent(e);
	}


}
