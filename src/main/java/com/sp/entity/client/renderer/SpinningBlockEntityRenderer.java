package com.sp.entity.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;

public class SpinningBlockEntityRenderer extends DisplayEntityRenderer.BlockDisplayEntityRenderer {
    private final BlockRenderManager blockRenderManager;

    public SpinningBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(DisplayEntity.BlockDisplayEntity blockDisplayEntity, DisplayEntity.BlockDisplayEntity.Data data, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.blockRenderManager.renderBlockAsEntity(data.blockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.translate(0.5f, 0.5f, 0.5f);
    }
}
