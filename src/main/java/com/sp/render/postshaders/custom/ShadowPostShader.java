package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.client.ClientDestructionEvents;
import com.sp.render.ShaderType;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ShadowPostShader extends PostShader {
    public static final Identifier SHADOWS_POST = DestroyingMinecraft.idOf("shadows");
    public static final Identifier SHADOWS_SHADER = DestroyingMinecraft.idOf("shadows/shadows");

    public ShadowPostShader() {
        super(SHADOWS_POST, SHADOWS_SHADER);
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        ShadowMapRenderer.setShadowUniforms(shaderProgram);

        if (Objects.requireNonNull(DestroyingMinecraftConfig.shaderType) == ShaderType.SUPERNOVA) {
            ClientDestructionEvents.SUPERNOVA_CLIENT.setUniforms(shaderProgram, tickDelta);
        } else {
            BetterUniforms.setFloat(shaderProgram, "flashTimer", 1.0f);
        }
    }
}
