package com.asiainfo.funf2020.presenter;

import android.content.Context;

/**
 * Created by Justin Z on 2016/10/12.
 * 502953057@qq.com
 */
public interface AudioRecordPresenter {

	/**
	 * 监听录制状态
	 */
	void listenAudioRecording();

	/**
	 * 监听内存卡中的缓存
	 */
	void listenSDCard(Context context);

	/**
	 * 开始录制或停止录制
	 * @param context Context
	 */
	void doRecording(Context context);

	/**
	 * 绑定录制服务
	 */
	void bindRecordingService(Context context);

	/**
	 * 解除录制服务绑定
	 */
	void unBindRecordingService(Context context);

	/**
	 * 清除缓存
	 */
	void delCache(Context context);



}
