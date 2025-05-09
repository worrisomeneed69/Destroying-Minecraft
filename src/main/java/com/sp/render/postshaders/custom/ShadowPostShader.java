package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.postshaders.PostShader;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShadowPostShader extends PostShader {
    public static final Identifier SHADOWS_POST = DestroyingMinecraft.idOf("shadows");
    public static final Identifier SHADOWS_SHADER = DestroyingMinecraft.idOf("shadows/shadows");

    public ShadowPostShader() {
        super(SHADOWS_POST, SHADOWS_SHADER, null);
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        ShadowMapRenderer.setShadowUniforms(shaderProgram);
        if (DestroyingMinecraftConfig.shaderType == DestroyingMinecraftConfig.ShaderType.SUPERNOVA) {
            DestroyingMinecraftClient.supernovaPostShader.getRenderTimer().setUniforms(shaderProgram, tickDelta);
        }
    }
}
