package com.asiainfo.funf2020.utils;


import android.content.Context;

import com.ai2020lab.aimedia.CameraManager;
import com.ai2020lab.aimedia.model.FlashMode;
import com.ai2020lab.aiutils.common.LogUtils;

/**
 * Created by Justin Z on 2016/10/19.
 * 502953057@qq.com
 */
public class CommonUtils {

	private final static String TAG = CommonUtils.class.getSimpleName();

	public static void setSplashLightOn(Context context) {
		int result = CameraManager.getInstance().openDriver(context, false);
		if (result == -1) {
			LogUtils.i(TAG, "打开相机失败，请重试");
			return;
		}
		CameraManager.getInstance().setFlashMode(FlashMode.TORCH);
		CameraManager.getInstance().startPreview(null);

	}


	public static void setSplashLightOff() {
		CameraManager.getInstance().stopPreview();
		CameraManager.getInstance().closeDriver();
	}


}
