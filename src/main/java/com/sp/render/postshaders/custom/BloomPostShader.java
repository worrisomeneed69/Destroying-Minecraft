package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BloomPostShader extends PostShader {
    public static final Identifier BLOOM_POST = DestroyingMinecraft.idOf("bloom");

    public static final Identifier[] BLUR_IDENTIFIERS = {
            DestroyingMinecraft.idOf("bloom/blur/horizontal"),
            DestroyingMinecraft.idOf("bloom/blur/vertical")
    };


    public BloomPostShader() {
        super(BLOOM_POST, BLOOM_POST);
    }

    @Override
    public void setUniforms(PostPipeline.Context context, float tickDelta, MinecraftClient client, World clientWorld) {
        for(Identifier identifier : BLUR_IDENTIFIERS) {
            ShaderProgram shaderProgram = context.getShader(identifier);
            if (shaderProgram != null) {
                BetterUniforms.setFloat(shaderProgram, "blurStrength", 1.0f);
                BetterUniforms.setFloat(shaderProgram, "xLimit", 0.53f);
            }
        }

    }
}
