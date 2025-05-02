package com.sp.entity.client.renderer;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class SpinningBlockEntityRenderer extends EntityRenderer<SpinningBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public SpinningBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(SpinningBlockEntity entity) {
        return null;
    }

    @Override
    public void render(SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(entity);

        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));

        matrices.translate(-0.5f, -0.5f, -0.5f);
        this.blockRenderManager.renderBlockAsEntity(component.getBlockState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.translate(0.5f, 0.5f, 0.5f);
    }
}
