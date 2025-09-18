package com.sp.mixin;

import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceFactory;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProgram.class)
public abstract class SetShadowUniformMixin {
    @Shadow
    public abstract @Nullable GlUniform getUniform(String name);

    @Unique public GlUniform renderingShadow;
    @Unique private int attempt;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setShadowUniform(ResourceFactory factory, String name, VertexFormat format, CallbackInfo ci) {
        this.renderingShadow = this.getUniform("renderingShadow");
    }

    @Inject(method = "initializeUniforms", at = @At("HEAD"))
    private void setRenderingShadow(VertexFormat.DrawMode drawMode, Matrix4f viewMatrix, Matrix4f projectionMatrix, Window window, CallbackInfo ci) {
        foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.getShader();

        int rendering = VeilLevelPerspectiveRenderer.isRenderingPerspective() ? 1 : 0;
        if (shader != null) {

            BetterUniforms.setInt(shader, "renderingShadow", rendering);
        }


        if (this.renderingShadow != null) {
            this.renderingShadow.set(rendering);
        } else if (attempt < 3) {
            attempt++;
            this.renderingShadow = this.getUniform("renderingShadow");
        }

    }

}