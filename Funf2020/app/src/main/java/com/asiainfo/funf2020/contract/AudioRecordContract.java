package com.asiainfo.funf2020.contract;

import android.content.Context;

import com.asiainfo.funf2020.mvp.base.BasePresenter;
import com.asiainfo.funf2020.mvp.base.BaseView;

/**
 * 接口协议类
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public interface AudioRecordContract {

	interface View extends BaseView {

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

	interface Presenter extends BasePresenter {
		void listenAudioRecording();

		void listenSDCard();

		void doRecording();

		void bindRecordingService();

		void unBindRecordingService();

		void delCache();
	}
}
