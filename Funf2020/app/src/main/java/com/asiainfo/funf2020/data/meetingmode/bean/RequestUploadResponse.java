package com.asiainfo.funf2020.data.meetingmode.bean;

import com.asiainfo.funf2020.data.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Z on 2016/10/15.
 * 502953057@qq.com
 */
public class RequestUploadResponse extends ResponseData<RequestUploadResponse.Result> {

	public class Result {

		@Expose
		@SerializedName("acceptYourRequest")
		public boolean result;
	}
}
