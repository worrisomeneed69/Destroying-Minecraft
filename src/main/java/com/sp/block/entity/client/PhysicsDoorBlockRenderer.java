package com.sp.block.entity.client;

import com.sp.block.entity.custom.PhysicsDoorBlockEntity;
import com.sp.util.RenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class PhysicsDoorBlockRenderer implements BlockEntityRenderer<PhysicsDoorBlockEntity> {

    public PhysicsDoorBlockRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(PhysicsDoorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.shouldShowSelection()) {
            boolean bl = entity.isSettingSelection();
            int[] colors = new int[4];
            colors[0] = bl ? 255 : 100;
            colors[1] = bl ? 100 : 255;
            colors[2] = 100;
            colors[3] = 100;

            matrices.translate(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ());
            RenderUtil.drawBlocksFromCorners(
                    matrices,
                    vertexConsumers,
                    null,
                    entity.getCorner1(),
                    entity.getCorner2(),
                    colors[0],
                    colors[1],
                    colors[2],
                    colors[3],
                    true);
        }
    }


}
