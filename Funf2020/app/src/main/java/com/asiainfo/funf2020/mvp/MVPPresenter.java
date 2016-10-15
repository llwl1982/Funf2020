package com.asiainfo.funf2020.mvp;

import com.asiainfo.funf2020.mvp.base.BaseView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Presenter基础接口类
 * Created by Justin Z on 2016/10/14.
 * 502953057@qq.com
 */
public abstract class MVPPresenter<V extends BaseView> {

	public Reference<V> mViewRef;

	/**
	 * 初始化Presenter
	 */
	public void attachView(V view) {
		mViewRef = new WeakReference<>(view);
	}

	/**
	 * 销毁Presenter持有的View引用
	 */
	public void detachView() {
		if (mViewRef != null) {
			mViewRef.clear();
			mViewRef = null;
		}
	}

	public V getView() {
		return mViewRef.get();
	}

}
