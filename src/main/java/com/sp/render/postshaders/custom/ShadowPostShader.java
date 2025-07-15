package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.render.ShaderType;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.postshaders.PostShader;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ShadowPostShader extends PostShader {
    public static final Identifier SHADOWS_POST = DestroyingMinecraft.idOf("shadows");
    public static final Identifier SHADOWS_SHADER = DestroyingMinecraft.idOf("shadows/shadows");

    public ShadowPostShader() {
        super(SHADOWS_POST, SHADOWS_SHADER, null);
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        ShadowMapRenderer.setShadowUniforms(shaderProgram);
        if (DestroyingMinecraftConfig.shaderType == ShaderType.SUPERNOVA) {
            DestroyingMinecraftClient.supernovaPostShader.getDestructionEvent().setUniforms(shaderProgram, tickDelta);
        }
    }
}
