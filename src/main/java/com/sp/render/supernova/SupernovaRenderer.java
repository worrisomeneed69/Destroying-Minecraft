package com.sp.render.supernova;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.MathHelper;

public class SupernovaRenderer {
    private static float sunSize;

    public static float getSunSize() {
        return MathHelper.sin(RenderSystem.getShaderGameTime() * 1000) * 30.0F;
    }

}
