package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import com.sp.render.rendertimers.PlanetRenderTimer;
import net.minecraft.util.Identifier;

public class PlanetPostShader extends PostShader {
    public static final Identifier PLANET_POST = DestroyingMinecraft.idOf("planet");
    public static final Identifier PLANET_SHADER = DestroyingMinecraft.idOf("planet/planet");

    public PlanetPostShader() {
        super(PLANET_POST, PLANET_SHADER, new PlanetRenderTimer(400));
    }
}
