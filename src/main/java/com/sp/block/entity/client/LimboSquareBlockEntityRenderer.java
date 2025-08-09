package com.sp.block.entity.client;

import com.sp.block.entity.custom.LimboSquareBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

public class LimboSquareBlockEntityRenderer implements BlockEntityRenderer<LimboSquareBlockEntity> {

    public LimboSquareBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(LimboSquareBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());

        float size = entity.getSize();
        Vector3f color = entity.getColor();

        matrices.translate(0.5f, -0.9f + entity.getHeight(), 0.5f);
        vertexConsumer.vertex(matrices.peek(),  size, 1, -size).color(color.x, color.y, color.z, 1.0f);
        vertexConsumer.vertex(matrices.peek(),  size, 1,  size).color(color.x, color.y, color.z, 1.0f);
        vertexConsumer.vertex(matrices.peek(), -size, 1,  size).color(color.x, color.y, color.z, 1.0f);
        vertexConsumer.vertex(matrices.peek(), -size, 1, -size).color(color.x, color.y, color.z, 1.0f);
    }
}
