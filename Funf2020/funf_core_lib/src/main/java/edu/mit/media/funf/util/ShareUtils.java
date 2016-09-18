package edu.mit.media.funf.util;

import android.content.Context;
import android.content.SharedPreferences;

import static edu.mit.media.funf.util.AsyncSharedPrefs.async;

/**
 * Created by Rocky on 16/9/18.
 */
public class ShareUtils {

    private static String IMEI_KEY = "IMEI_KEY";

    private static String imei;


    public static void setImei(String imei, Context context) {
        ShareUtils.imei = imei;
        SharedPreferences prefs = async(context.getSharedPreferences(UuidUtil.FUNF_UTILS_PREFS, Context.MODE_PRIVATE));

        prefs.edit().putString(IMEI_KEY, imei).commit();
    }

    public static String getImei() {
        return imei;
    }
}
