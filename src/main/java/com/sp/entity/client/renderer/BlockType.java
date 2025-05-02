package com.sp.entity.client.renderer;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

public enum BlockType {
    SINGLE(BlockType::renderSingle),
    DOUBLE(BlockType::renderDouble),
    TRIPLE(BlockType::renderTriple);

    private final BlockType.Render render;

    BlockType(BlockType.Render render) {
        this.render = render;
    }


    public void render(BlockRenderManager blockRenderManager, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        render.render(blockRenderManager, entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private static void renderSingle(BlockRenderManager blockRenderManager, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(entity);
        float scale = component.getScale();
        float halfScale = scale/2;
        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);
    }

    private static void renderDouble(BlockRenderManager blockRenderManager, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(entity);
        float scale = component.getScale();
        float halfScale = scale/2;
        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);

        matrices.translate(scale, 0, 0);

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);

        matrices.translate(-scale, 0, 0);
    }

    private static void renderTriple(BlockRenderManager blockRenderManager, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(entity);
        float scale = component.getScale();
        float halfScale = scale/2;
        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);

        matrices.translate(scale, 0, 0);

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);

        matrices.translate(0, 0, scale);

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);

        matrices.translate(-scale, 0, -scale);
    }

    private interface Render {
        void render(BlockRenderManager blockRenderManager, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);
    }
}
