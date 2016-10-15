package com.asiainfo.funf2020.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.asiainfo.funf2020.mvp.base.BaseView;

/**
 * Created by Justin Z on 2016/10/14.
 * 502953057@qq.com
 */
public abstract class MVPActivity<V extends BaseView, P extends MVPPresenter>
		extends AppCompatActivity {

	public P mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMVP();
	}

	@SuppressWarnings("unchecked")
	public void initMVP() {
		mPresenter = initPresenter();
		// 这里一定是传入泛型
		mPresenter.attachView((V) this);
	}

	/**
	 * 实例化Presenter
	 */
	public abstract P initPresenter();

	public P getPresenter(){
		return mPresenter;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPresenter.detachView();
	}

}
