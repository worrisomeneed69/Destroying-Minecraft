package com.sp.mixin;

import com.sp.render.PerspectiveRenderer;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProgram.class)
public abstract class SetShadowUniformMixin {

    @Inject(method = "initializeUniforms", at = @At("HEAD"))
    private void setRenderingShadow(VertexFormat.DrawMode drawMode, Matrix4f viewMatrix, Matrix4f projectionMatrix, Window window, CallbackInfo ci) {
        foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.getShader();
        if (shader != null) {
            BetterUniforms.setInt(shader, "renderingShadow", VeilLevelPerspectiveRenderer.isRenderingPerspective() ? 1 : 0);
        }

    }

}