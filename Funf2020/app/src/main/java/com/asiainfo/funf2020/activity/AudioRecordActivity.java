package com.asiainfo.funf2020.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ai2020lab.aiutils.common.LogUtils;
import com.asiainfo.funf2020.R;
import com.asiainfo.funf2020.contract.AudioRecordContract;
import com.asiainfo.funf2020.presenter.AudioRecordPresenter;
import com.asiainfo.funf2020.mvp.MVPActivity;

import edu.mit.media.funf.util.LogUtil;

/**
 * 录音界面
 */
public class AudioRecordActivity extends MVPActivity<AudioRecordContract.View,
		AudioRecordPresenter> implements AudioRecordContract.View {

	private final static String TAG = AudioRecordActivity.class.getSimpleName();

	private Button scanNowButton;

	private Button clearCacheButton;

	private Button meetingModeButton;

	private boolean isAtBack = false;

	/**
	 * 初始化界面
	 */
	private void initViews() {
		scanNowButton = (Button) findViewById(R.id.record_audio);
		clearCacheButton = (Button) findViewById(R.id.clear_cache);
		meetingModeButton = (Button) findViewById(R.id.meeting_mode);
		scanNowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPresenter().doRecording();
			}
		});
		clearCacheButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPresenter().delCache();
			}
		});
		meetingModeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPresenter().toMeetingModeActivity();
			}
		});
	}

	/**
	 * 检查状态后更新录制按钮文字
	 */
	@Override
	public void onListenRecording(boolean isRunning) {
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
	 * 用于设置扫描按钮的文字
	 *
	 * @param text 扫描按钮的文字
	 */
	@Override
	public void setScanNowButtonText(String text) {
		scanNowButton.setText(text);
	}

	/**
	 * 用于设置清除缓存按钮的文字
	 *
	 * @param text 清除缓存按钮的文字
	 */
	@Override
	public void setSDCardButtonText(String text) {
		clearCacheButton.setText(text);
	}

	/**
	 * 设置扫描按钮显示或隐藏
	 */
	@Override
	public void setScanNowButtonVisibility(int visibility) {
		scanNowButton.setVisibility(visibility);
	}

	@Override
	public boolean isAtBack() {
		LogUtils.i(TAG, "--判断 AudioRecordActivity 是否在后台--");
		return isAtBack;
	}

	@Override
	protected void onStart() {
		super.onStart();
		setScanNowButtonVisibility(View.GONE);
		isAtBack = false;
		// 检测到service已经在运行则同Service建立连接
		getPresenter().bindRecordingService();
		// 用户进入界面开始监听服务状态
		getPresenter().listenAudioRecording();
		// 监听缓存容量
		getPresenter().listenSDCard();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioRecordActivity", "--重新创建AudioRecordActivity");
		setContentView(R.layout.activity_audio_record);
		initViews();

	}

	@Override
	protected void onStop() {
		super.onStop();
		setScanNowButtonVisibility(View.GONE);
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
	}

	/**
	 * 初始化Presenter
	 */
	@Override
	public AudioRecordPresenter initPresenter() {
		return new AudioRecordPresenter(this);
	}


}
