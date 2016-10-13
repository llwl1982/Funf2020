package com.asiainfo.funf2020.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 此类用来解决Handler引起的内存溢出
 * Created by Justin Z on 2016/10/13.
 * 502953057@qq.com
 */
public class WeakReferenceHandler<T extends Handler.Callback> extends Handler {

	private T handlerContainer;
	private WeakReference<T> handlerReference;

	/**
	 * 构造方法
	 */
	public WeakReferenceHandler(T handlerContainer) {
		handlerReference = new WeakReference<>(handlerContainer);
	}

	public T getHandlerContainer() {
		return handlerReference.get();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Callback container = getHandlerContainer();
		if(container != null){
			container.handleMessage(msg);
		}

	}
}
