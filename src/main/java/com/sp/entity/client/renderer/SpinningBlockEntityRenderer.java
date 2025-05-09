package com.sp.entity.client.renderer;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

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
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(entity);
        component.getBlockType().render(this.blockRenderManager, this.entityRenderDispatcher, entity, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);

//        this.renderAnimal(entity, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);


    }

    private void renderAnimal(SpinningBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d cameraPos = this.entityRenderDispatcher.camera.getPos();

        Vec3d pos = entity.getPos().subtract(cameraPos);

        matrices.translate(pos.x, pos.y, pos.z);
        matrices.translate(0, 0.5, 0);
        matrices.multiply(new Quaternionf().rotateXYZ((float) Math.toRadians(entity.getPitch(tickDelta)), (float) Math.toRadians(entity.getYaw(tickDelta)), 0));
        matrices.translate(0, -0.5, 0);
        matrices.translate(-pos.x, -pos.y, -pos.z);

//        EnderDragonEntity pig = EntityType.ENDER_DRAGON.create(entity.getWorld());
//        CowEntity pig = EntityType.COW.create(entity.getWorld());
        PigEntity pig = EntityType.PIG.create(entity.getWorld());
        this.entityRenderDispatcher.render(pig, entity.getX() - cameraPos.x, entity.getY() - cameraPos.y, entity.getZ() - cameraPos.z, yaw, tickDelta, matrices, vertexConsumers, 14 << 20);
        pig.discard();
    }

}
