package com.asiainfo.funf2020.contract;

import android.content.Context;

import com.asiainfo.funf2020.mvp.base.BasePresenter;
import com.asiainfo.funf2020.mvp.base.BaseView;

/**
 * 会议模式接口协议类
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public interface MeetingModeContract {

	interface View extends BaseView {
		/**
		 * 检查录音状态后控制屏幕闪烁
		 */
		void onListenRecording(boolean isRunning);

		/**
		 * 控制屏幕闪烁
		 */
		void setSplashOn();

		void setSplashOff();

		void setDoubleTapText(String text);

		void setDoubleTapTextColor(int color);

		/**
		 * 判断界面是否在后台
		 */
		boolean isAtBack();

	}

	interface Presenter extends BasePresenter {

		/**
		 * 开始录制或结束录制
		 */
		void doRecording();

		/**
		 * 监听录制状态
		 */
		void listenAudioRecording();

		void bindRecordingService();

		void unBindRecordingService();


	}
}
