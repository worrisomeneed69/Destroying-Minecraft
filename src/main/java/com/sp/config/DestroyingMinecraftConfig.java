package com.sp.config;

import com.sp.render.ShaderType;
import eu.midnightdust.lib.config.MidnightConfig;

public class DestroyingMinecraftConfig extends MidnightConfig {
    public static final String SHADERS = "shaders";

    @Entry(category = SHADERS)
    public static ShaderType shaderType = ShaderType.CRACKS;

//    @Entry(category = SHADERS)
//    public static Comment spacer1;

    @Entry(category = SHADERS)
    public static boolean enableDepthOfField = false;

    @Entry(category = SHADERS, isSlider = true, min = 0.1f, max = 3.0f, precision = 10)
    public static float blurStrength = 1f;

    @Entry(category = SHADERS, isSlider = true, min = 0.1f, max = 0.9f, precision = 10)
    public static float autoFocusTime = 0.8f;
}
