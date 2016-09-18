package com.asiainfo.funf2020.app;

import android.app.Application;

import com.ai2020lab.aiutils.system.DeviceUtils;
import edu.mit.media.funf.util.ShareUtils;

/**
 * Created by Rocky on 16/9/18.
 */
public class AIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShareUtils.setImei(DeviceUtils.getIMEI(this), this);
    }
}
