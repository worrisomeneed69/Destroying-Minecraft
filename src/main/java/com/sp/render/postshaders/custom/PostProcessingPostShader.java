package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import com.sp.util.MathUtil;
import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class PostProcessingPostShader extends PostShader {
    public static final Identifier POST = DestroyingMinecraft.idOf("post");
    public static final Identifier SHADER = DestroyingMinecraft.idOf("post/post");
    public static final Identifier[] BLUR_IDENTIFIERS = {
            DestroyingMinecraft.idOf("bloom/blur/horizontal"),
            DestroyingMinecraft.idOf("bloom/blur/vertical")
    };
    float smoothDepth;

    public PostProcessingPostShader() {
        super(POST, SHADER, null);
    }

    @Override
    public void setUniforms(PostPipeline.Context context, float tickDelta, MinecraftClient client, World clientWorld) {
        super.setUniforms(context, tickDelta, client, clientWorld);

        for(Identifier identifier : BLUR_IDENTIFIERS) {
            ShaderProgram shaderProgram = context.getShader(identifier);
            if (shaderProgram != null) {
                BetterUniforms.setFloat(shaderProgram, "blurStrength", DestroyingMinecraftConfig.blurStrength);
                BetterUniforms.setFloat(shaderProgram, "xLimit", DestroyingMinecraftConfig.enableDepthOfField ? 1.0f : -0.1f);
            }
        }
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        float farPlane = 100;
        HitResult hitResult = client.getCameraEntity().raycast(farPlane, tickDelta, true);

        float depth = (float) client.getCameraEntity().getEyePos().distanceTo(hitResult.getPos()) / farPlane;

        //Smooths down the Depth at the crosshair so it gives a "not so instant" autofocus effect
        this.smoothDepth = MathUtil.Lerp(this.smoothDepth, depth, DestroyingMinecraftConfig.autoFocusTime, MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration());

        BetterUniforms.setFloat(shaderProgram, "centerDepth", this.smoothDepth);

        BetterUniforms.setInt(shaderProgram, "enabled", DestroyingMinecraftConfig.enableDepthOfField ? 1 : 0);
    }
}
