package com.asiainfo.funf2020.view;

/**
 * Created by Justin Z on 2016/10/11.
 * 502953057@qq.com
 */
public interface AudioRecordView {


	/**
	 * 检查录音状态后更新界面
	 */
	void onListenRecording(boolean isRunning);


	/**
	 * 设置录音按钮的文字显示
	 */
	void setScanNowButtonText(String text);

	/**
	 * 设置清除缓存按钮文字显示
	 */
	void setSDCardButtonText(String text);

	/**
	 * 设置录音按钮隐藏或显示
	 */
	void setScanNowButtonVisibility(int visibility);


	boolean isAtBack();



}
