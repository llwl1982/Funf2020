package com.asiainfo.funf2020.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Rocky on 16/6/1.
 */
public class AssetsUtils {

    public static String getFromAsset(Context context, String filePath) {
        StringBuilder sb = new StringBuilder();
        AssetManager assetManager = context.getResources().getAssets();

        try {
            InputStreamReader inputReader = new InputStreamReader(
                    assetManager.open(filePath));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                sb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
