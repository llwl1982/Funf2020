package com.asiainfo.funf2020.utils.net;

/**
 * Created by Rocky on 15/7/17.
 * 接口Url
 */
public enum UrlName {
	// 结束上传
	FINISH_UPLOAD("finishupload"),
	// 请求是否可以上传
	REQUEST_UPLOAD("requestupload"),

	// 用于测试
	REQUEST_SBOSS_TEST("queryMostPopularWork");

	private String name;

	UrlName(String name) {
		this.name = name;
	}

	public String getUrl() {
//		return "http://" + HOST + ":" + PORT + "/" + "nsboss/workopportunity/" + name;
		return "http://" + HOST + ":" + PORT + "/" + name;
	}

//	private static final String HOST = "171.221.254.231";
//	private static final String HOST = "10.5.1.246";
	private static final String HOST = "171.221.254.231";
	private static final int PORT = 2002;//8011

}
