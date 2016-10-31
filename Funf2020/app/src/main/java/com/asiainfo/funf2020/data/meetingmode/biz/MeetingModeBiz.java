package com.asiainfo.funf2020.data.meetingmode.biz;

import android.content.Context;

import com.asiainfo.funf2020.data.meetingmode.bean.FinishUploadRequest;
import com.asiainfo.funf2020.data.meetingmode.bean.RequestUploadRequest;
import com.asiainfo.funf2020.utils.net.HttpManager;
import com.asiainfo.funf2020.utils.net.UrlName;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public class MeetingModeBiz implements IMeetingModeBiz{

	private Context context;


	public MeetingModeBiz(Context context){
		this.context = context;
	}

	/**
	 * 发送上传查询请求
	 */
	@Override
	public void requestUpload(ResponseHandlerInterface response) {
		RequestUploadRequest data = new RequestUploadRequest();
		HttpManager.postJson(context, UrlName.REQUEST_UPLOAD.getUrl(), data,
				response);
	}

	/**
	 * 发送取消上传请求
	 */
	@Override
	public void finishUpload(ResponseHandlerInterface response) {
		FinishUploadRequest data = new FinishUploadRequest();
		HttpManager.postJson(context, UrlName.FINISH_UPLOAD.getUrl(), data,
				response);
	}
}
