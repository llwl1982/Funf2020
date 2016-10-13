package com.asiainfo.funf2020;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.asiainfo.funf2020.presenter.AudioRecordPresenter;
import com.asiainfo.funf2020.presenter.AudioRecordPresenterCompl;
import com.asiainfo.funf2020.view.AudioRecordView;


public class AudioRecordActivity extends AppCompatActivity implements AudioRecordView {

	private final static String TAG = AudioRecordActivity.class.getSimpleName();

	private Button scanNowButton;

	private Button clearCacheButton;

	private boolean isAtBack = false;

	/**
	 * Presenter
	 */
	private AudioRecordPresenter mAudioRecordPresenter;

	/**
	 * 初始化界面
	 */
	private void initViews() {
		scanNowButton = (Button) findViewById(R.id.record_audio);
		clearCacheButton = (Button) findViewById(R.id.clear_cache);
		scanNowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAudioRecordPresenter.doRecording(AudioRecordActivity.this);
			}
		});
		clearCacheButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAudioRecordPresenter.delCache(AudioRecordActivity.this);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioRecordActivity", "--重新创建AudioRecordActivity");
		setContentView(R.layout.activity_audio_record);
		initViews();

		// 初始化Presenter
		mAudioRecordPresenter = new AudioRecordPresenterCompl(this, this);

	}


	@Override
	protected void onStart() {
		super.onStart();
		setScanNowButtonVisibility(View.GONE);
		isAtBack = false;
		// 检测到service已经在运行则同Service建立连接
		mAudioRecordPresenter.bindRecordingService(this);
		// 用户进入界面开始监听服务状态
		mAudioRecordPresenter.listenAudioRecording();
		// 监听缓存容量
		mAudioRecordPresenter.listenSDCard(this);
	}


	/**
	 * 检查状态后更新录制按钮文字
	 */
	@Override
	public void onListenRecording(boolean isRunning){
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
	public void setSDCardButtonText(String text){
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
	public boolean isAtBack(){
		return isAtBack;
	}


	@Override
	protected void onStop() {
		super.onStop();
		setScanNowButtonVisibility(View.GONE);
		isAtBack = true;
		Log.i(TAG, "--AudioRecordActivity回到后台");
		// 界面回到后台，如果服务还在则同服务解除绑定
		mAudioRecordPresenter.unBindRecordingService(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "--AudioRecordActivity挂了");
		// 解除与Service的绑定
		mAudioRecordPresenter.unBindRecordingService(this);
	}
}
