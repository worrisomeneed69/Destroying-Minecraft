package com.sp.entity.client.renderer;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public enum BlockType {
    SINGLE(BlockType::renderSingle),
    PIG(BlockType::renderPig),
    COW(BlockType::renderCow),
    CHICKEN(BlockType::renderChicken);
//    DOUBLE(BlockType::renderDouble),
//    TRIPLE(BlockType::renderTriple);

    private final BlockType.Render render;

    BlockType(BlockType.Render render) {
        this.render = render;
    }


    public void render(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        render.render(blockRenderManager, entityRenderDispatcher, entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private static void renderSingle(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = entity.getComponent();
        float scale = component.getScale();
        float halfScale = scale/2;
//        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_X.rotationDegrees(component.getPitch(tickDelta))));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_Y.rotationDegrees(component.getYaw(tickDelta))));

        matrices.translate(-halfScale, -halfScale, -halfScale);
        matrices.scale(scale,scale,scale);
        blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1/scale, 1/scale, 1/scale);
        matrices.translate(halfScale, halfScale, halfScale);
    }

    private static void renderPig(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d cameraPos = startRenderingAnimal(entityRenderDispatcher, entity, tickDelta, matrices);

        PigEntity pig = EntityType.PIG.create(entity.getWorld());
        entityRenderDispatcher.render(pig, entity.getX() - cameraPos.x, entity.getY() - cameraPos.y, entity.getZ() - cameraPos.z, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);
        pig.discard();
    }

    private static void renderCow(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d cameraPos = startRenderingAnimal(entityRenderDispatcher, entity, tickDelta, matrices);

        CowEntity cow = EntityType.COW.create(entity.getWorld());
        entityRenderDispatcher.render(cow, entity.getX() - cameraPos.x, entity.getY() - cameraPos.y, entity.getZ() - cameraPos.z, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);
        cow.discard();
    }

    private static void renderChicken(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d cameraPos = startRenderingAnimal(entityRenderDispatcher, entity, tickDelta, matrices);

        ChickenEntity chicken = EntityType.CHICKEN.create(entity.getWorld());
        entityRenderDispatcher.render(chicken, entity.getX() - cameraPos.x, entity.getY() - cameraPos.y, entity.getZ() - cameraPos.z, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);
        chicken.discard();
    }

    private static Vec3d startRenderingAnimal(EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float tickDelta, MatrixStack matrices) {
        SpinningBlockComponent component = entity.getComponent();
        Vec3d cameraPos = entityRenderDispatcher.camera.getPos();

        Vec3d pos = entity.getPos().subtract(cameraPos);

        matrices.translate(pos.x, pos.y, pos.z);
        matrices.translate(0, 0.5, 0);
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_X.rotationDegrees(component.getPitch(tickDelta))));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_Y.rotationDegrees(component.getYaw(tickDelta))));
        matrices.translate(0, -0.5, 0);
        matrices.translate(-pos.x, -pos.y, -pos.z);

        return cameraPos;
    }

    private static void renderDouble(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = entity.getComponent();
        float scale = component.getScale();
        float halfScale = scale/2;
//        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_X.rotationDegrees(component.getPitch(tickDelta))));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_Y.rotationDegrees(component.getYaw(tickDelta))));

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

    private static void renderTriple(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = entity.getComponent();
        float scale = component.getScale();
        float halfScale = scale/2;
//        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_X.rotationDegrees(component.getPitch(tickDelta))));
        matrices.multiply(new Quaternionf().set(RotationAxis.POSITIVE_Y.rotationDegrees(component.getYaw(tickDelta))));

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
        void render(BlockRenderManager blockRenderManager, EntityRenderDispatcher entityRenderDispatcher, SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);
    }
}
