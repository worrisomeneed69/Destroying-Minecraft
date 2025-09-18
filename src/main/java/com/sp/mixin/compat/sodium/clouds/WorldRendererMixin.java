package com.sp.mixin.compat.sodium.clouds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    private int ticks;

    @Shadow
    private int lastCloudsBlockX;

    @Shadow
    private int lastCloudsBlockY;

    @Shadow
    private int lastCloudsBlockZ;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private @Nullable CloudRenderMode lastCloudRenderMode;

    @Shadow
    private Vec3d lastCloudsColor;

    @Shadow
    private boolean cloudsDirty;

    @Shadow
    private @Nullable VertexBuffer cloudsBuffer;

    @Shadow
    protected abstract BuiltBuffer buildCloudsBuffer(Tessellator tessellator, double x, double y, double z, Vec3d color);

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V"))
    private void renderOldClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, Operation<Void> original) {
        if (this.lastCloudRenderMode == CloudRenderMode.FANCY || client.options.getCloudRenderModeValue() == CloudRenderMode.FANCY) {
            client.options.getCloudRenderMode().setValue(CloudRenderMode.FAST);
            this.lastCloudRenderMode = CloudRenderMode.FAST;
        }
        if (!VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            this.renderCloudsTheSequel(matrices, matrix4f, matrix4f2, tickDelta, cameraX, cameraY, cameraZ);
        }

    }

    @Unique
    private void renderCloudsTheSequel(MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        float f = this.world.getDimensionEffects().getCloudsHeight();
        if (!Float.isNaN(f)) {
            double e = (this.ticks + tickDelta) * 0.03F;
            double i = (cameraX + e) / 12.0;
            double j = f - (float)cameraY + 0.33F;
            double k = cameraZ / 12.0 + 0.33F;
            i -= MathHelper.floor(i / 2048.0) * 2048;
            k -= MathHelper.floor(k / 2048.0) * 2048;
            float l = (float)(i - MathHelper.floor(i));
            float m = (float)(j / 4.0 - MathHelper.floor(j / 4.0)) * 4.0F;
            float n = (float)(k - MathHelper.floor(k));
            Vec3d vec3d = this.world.getCloudsColor(tickDelta);
            int o = (int)Math.floor(i);
            int p = (int)Math.floor(j / 4.0);
            int q = (int)Math.floor(k);
            if (o != this.lastCloudsBlockX
                    || p != this.lastCloudsBlockY
                    || q != this.lastCloudsBlockZ
                    || this.client.options.getCloudRenderModeValue() != this.lastCloudRenderMode
                    || this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4) {
                this.lastCloudsBlockX = o;
                this.lastCloudsBlockY = p;
                this.lastCloudsBlockZ = q;
                this.lastCloudsColor = vec3d;
                this.lastCloudRenderMode = this.client.options.getCloudRenderModeValue();
                this.cloudsDirty = true;
            }

            if (this.cloudsDirty) {
                this.cloudsDirty = false;
                if (this.cloudsBuffer != null) {
                    this.cloudsBuffer.close();
                }

                this.cloudsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.cloudsBuffer.bind();
                this.cloudsBuffer.upload(this.buildCloudsBuffer(Tessellator.getInstance(), i, j, k, vec3d));
                VertexBuffer.unbind();
            }

            BackgroundRenderer.applyFogColor();
            matrices.push();
            matrices.multiplyPositionMatrix(matrix4f);
            matrices.scale(12.0F, 1.0F, 12.0F);
            matrices.translate(-l, m, -n);
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.bind();

                RenderLayer renderLayer = RenderLayer.getFastClouds();
                renderLayer.startDrawing();
                ShaderProgram shaderProgram = RenderSystem.getShader();
                this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), matrix4f2, shaderProgram);
                renderLayer.endDrawing();

                VertexBuffer.unbind();
            }

            matrices.pop();
        }
    }
}