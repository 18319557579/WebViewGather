package com.example.middleagent.config;

import android.content.Context;

import com.example.middleagent.R;

public class DefaultConfig {
    /**
     * 获得默认的配置信息
     */
    public static WebUtilsConfig getDefaultConfig(Context context) {
        return new WebUtilsConfig()
                .setBackText("返回")
                .setBackBtnRes(R.drawable.arrow_left_white)
                .setMoreBtnRes(R.drawable.more_web)
                .setShowBackText(true)
                .setShowMoreBtn(true)
                .setShowTitleLine(false)
                .setShowTitleView(true)
                .setTitleBackgroundColor(R.color.app_theme_color_green)
                .setTitleBackgroundRes(-1)
                .setTitleLineColor(-1)
                .setUseCoolIndicator(true)
                .setErrorLayoutId(R.layout.middleagent_error_page)
                .setClickRefreshResId(R.id.middleagent_btn_refresh);

    }
}
