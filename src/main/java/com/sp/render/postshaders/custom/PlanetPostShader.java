package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import com.sp.render.rendertimers.planet.PlanetRenderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PlanetPostShader extends PostShader {
    public static final Identifier PLANET_POST = DestroyingMinecraft.idOf("planet");
    public static final Identifier PLANET_SHADER = DestroyingMinecraft.idOf("planet/planet");

    public PlanetPostShader() {
        super(PLANET_POST, PLANET_SHADER, new PlanetRenderTimer(400));
    }
}
