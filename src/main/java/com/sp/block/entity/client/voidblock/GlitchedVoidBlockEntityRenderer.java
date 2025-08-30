package com.sp.block.entity.client.voidblock;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.block.entity.custom.voidblock.GlitchedVoidBlockEntity;
import com.sp.block.entity.custom.voidblock.VoidBlockEntity;
import com.sp.render.CustomRenderLayersAndVertexFormats;
import com.sp.util.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.SimplexNoise;

public class GlitchedVoidBlockEntityRenderer implements BlockEntityRenderer<GlitchedVoidBlockEntity> {
    private static final Random random = Random.create();
    private static final java.util.Random random2 = new java.util.Random();
    private static float startTime;
    private static float fadeTime;
    private static float rng;

    private static float currentBrightness;
    private static float targetBrightness;
    private static final BlockPos breakPos = new BlockPos(-29, 281, -60);
    private static float noiseAtPos = SimplexNoise.noise(-29, 281, -60);

    public GlitchedVoidBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(GlitchedVoidBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
    }

    private void renderCube(GlitchedVoidBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        renderFace(entity, matrix, buffer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(GlitchedVoidBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {

        GlitchedVoidBlockEntity originalEntity = entity.originalEntity;
        float brightness = 1.0f;

        if (originalEntity != null) {
            brightness = originalEntity.currentBrightness;

            if (originalEntity.equals(entity)) {
                if (entity.rng >= 0.9) {
                    float fade = (RenderSystem.getShaderGameTime() - entity.startTime) / entity.fadeTime;
//
                    if (fade >= entity.targetBrightness) {
                        entity.rng = random.nextFloat();
                        brightness = entity.baseBrightness;
                    } else {
                        brightness = MathHelper.lerp(fade, entity.baseBrightness, entity.targetBrightness);
                    }
//                brightness = entity.baseBrightness + 0.05f;
                } else {
                    brightness = entity.baseBrightness;
                    entity.currentBrightness = entity.baseBrightness;
                    entity.startTime = RenderSystem.getShaderGameTime();
                    entity.targetBrightness = random2.nextFloat(0.1f, 0.9f);
                    entity.rng = random.nextFloat();
                }

            }
            entity.currentBrightness = brightness;
        }


//        float brightness = chance <= 0.05f ? 0.9f : 1.0f;
//        float brightness = 1.0f;
        buffer.vertex(matrix, f, h, j).color(brightness, brightness, brightness, 1.0f);
        buffer.vertex(matrix, g, h, k).color(brightness, brightness, brightness, 1.0f);
        buffer.vertex(matrix, g, i, l).color(brightness, brightness, brightness, 1.0f);
        buffer.vertex(matrix, f, i, m).color(brightness, brightness, brightness, 1.0f);
    }

    protected RenderLayer getLayer() {
        return CustomRenderLayersAndVertexFormats.VOID_BLOCK;
    }

    @Override
    public int getRenderDistance() {
        return 300;
    }
}
