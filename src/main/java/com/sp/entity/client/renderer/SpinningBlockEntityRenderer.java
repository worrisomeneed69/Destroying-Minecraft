package com.sp.entity.client.renderer;

import com.sp.cca.custom.entity.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpinningBlockEntityRenderer extends EntityRenderer<SpinningBlockEntity> {
    private final BlockRenderManager blockRenderManager;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public SpinningBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
        this.entityRenderDispatcher = context.getRenderDispatcher();
    }

    @Override
    public Identifier getTexture(SpinningBlockEntity entity) {
        return null;
    }

    @Override
    public void render(SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SpinningBlockComponent component = entity.getComponent();
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        matrices.translate(-(d - cameraPos.x), -(e - cameraPos.x), -(f - cameraPos.x));

        Vec3d entityPos = entity.getLerpedPos(tickDelta);
        matrices.translate((entityPos.x - cameraPos.x), (entityPos.y - cameraPos.x), (entityPos.z - cameraPos.x));

        component.getBlockType().render(this.blockRenderManager, this.entityRenderDispatcher, entity, yaw, tickDelta, matrices, vertexConsumers,  LightmapTextureManager.pack(1, 15));
    }

}
