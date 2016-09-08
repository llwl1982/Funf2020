/**
 * 
 * Funf: Open Sensing Framework
 * Copyright (C) 2010-2011 Nadav Aharony, Wei Pan, Alex Pentland.
 * Acknowledgments: Alan Gardner
 * Contact: nadav@media.mit.edu
 * 
 * This file is part of Funf.
 * 
 * Funf is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Funf is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Funf. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package edu.mit.media.funf.util;

import android.util.Log;

public class LogUtil {

	public final static String TAG = "funf";

	private static boolean isDebug = true;

	/**
	 * 返回当前日志状态
	 *
	 * @return true-打印日志，false-不打印日志
	 */
	public static boolean getDebugFlag() {
		return isDebug;
	}

	/**
	 * 调用这个方法打开日志或关闭日志
	 *
	 * @param debug true-打开日志，false-关闭日志
	 */
	public static void setDebugFlag(boolean debug) {
		isDebug = debug;
	}

	/**
	 * 对应Log.d
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	/**
	 * 对应Log.e
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	/**
	 * 对应Log.e
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param e   异常信息
	 */
	public static void e(String tag, String msg, Exception e) {
		if (isDebug) {
			Log.e(tag, msg, e);
		}
	}

	/**
	 * 对应Log.i
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void w(String tag, String msg) {
		if (isDebug) {
			Log.w(tag, msg);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param e   异常信息
	 */
	public static void w(String tag, String msg, Exception e) {
		if (isDebug) {
			Log.w(tag, msg, e);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param tr  异常信息
	 */
	public static void w(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.w(tag, msg, tr);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param e   异常信息
	 */
	public static void w(String tag, Exception e) {
		if (isDebug) {
			Log.w(tag, e);
		}
	}

	/**
	 * 对应Log.v
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.v(tag, msg);
		}
	}
	
}
