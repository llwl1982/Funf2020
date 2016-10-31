package com.asiainfo.funf2020.data.meetingmode.biz;

import com.asiainfo.funf2020.mvp.base.BaseModel;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public interface IMeetingModeBiz extends BaseModel {


	void requestUpload(ResponseHandlerInterface response);

	void finishUpload(ResponseHandlerInterface response);
}
