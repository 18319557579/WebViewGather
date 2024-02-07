package com.example.webviewrapid.upper_ui;

import com.example.floatlayer.Config;

public class JumpFloatLayerParams {
    public static String jumpLabel = "JUMP_LABEL";

    public static Config getJumpConfig() {
        Config config = new Config();
        config.lengthType = false;
        config.size = 0.8F;

        config.radius = Float.MAX_VALUE;

        config.horizontalLocation = Config.HORIZONTAL_CENTER;
        config.verticalLocation = Config.VERTICAL_BOTTOM;
        config.verticalMargin = 100;

        config.dismissAnimRes = com.example.floatlayer.R.anim.flla_layer_dismiss_anim_2;
        config.delayMillis = 3500;

        return config;
    }
}
