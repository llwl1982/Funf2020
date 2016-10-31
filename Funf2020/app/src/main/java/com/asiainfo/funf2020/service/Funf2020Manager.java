package com.asiainfo.funf2020.service;

import com.asiainfo.funf2020.utils.AssetsUtils;

import edu.mit.media.funf.FunfManager;

/**
 * Created by Rocky on 16/6/1.
 */
public class Funf2020Manager extends FunfManager {

    @Override
    protected String createPipelineConfig(String configName) {
        return AssetsUtils.getFromAsset(this, configName);
    }
}
