package com.sp.entity.client.renderer;

import com.sp.entity.custom.MeteorEntity;
import com.sp.mixininterfaces.BufferBuilderPosition;
import com.sp.render.PerspectiveRenderer;
import com.sp.render.CustomRenderLayersAndVertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MeteorEntityRenderer extends EntityRenderer<MeteorEntity> {
    public MeteorEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(MeteorEntity entity) {
        return null;
    }

    @Override
    public void render(MeteorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(PerspectiveRenderer.isRenderingPerspective()) return;
        VertexConsumer bufferBuilder = vertexConsumers.getBuffer(CustomRenderLayersAndVertexFormats.METEOR);

        float pMinX = 0.0f;
        float pMinY = 0.0f;
        float pMinZ = 0.0f;

        float pMaxX = 1.0f;
        float pMaxY = 1.0f;
        float pMaxZ = 1.0f;

        float scale = 18f;

        matrices.push();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), camera.getPos().toVector3f().sub(entity.getPos().toVector3f()).normalize()));

        matrices.translate(0.0f, 0.5f, 3f);
        matrices.scale(scale, scale, scale);
        matrices.translate(-0.5f, -0.5f, -1.0f);


        MatrixStack.Entry entry = matrices.peek();
        Vec3d entityPos = entity.getLerpedPos(tickDelta).add(0, 0.5, 0);
        float id = entity.getId();

        bufferBuilder.vertex(entry, pMinX, pMaxY, pMaxZ).color(1.0f, 1.0f, 1.0f, Math.min((float) entity.age / 30, 1.0f));
        this.putPosition(bufferBuilder, entityPos, id);

        bufferBuilder.vertex(entry, pMinX, pMinY, pMaxZ).color(1.0f, 1.0f, 1.0f, Math.min((float) entity.age / 30, 1.0f));
        this.putPosition(bufferBuilder, entityPos, id);

        bufferBuilder.vertex(entry, pMaxX, pMinY, pMaxZ).color(1.0f, 1.0f, 1.0f, Math.min((float) entity.age / 30, 1.0f));
        this.putPosition(bufferBuilder, entityPos, id);

        bufferBuilder.vertex(entry, pMaxX, pMaxY, pMaxZ).color(1.0f, 1.0f, 1.0f, Math.min((float) entity.age / 30, 1.0f));
        this.putPosition(bufferBuilder, entityPos, id);



        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void putPosition(VertexConsumer bufferBuilder, Vec3d entityPos, float value) {
        if(bufferBuilder instanceof BufferBuilderPosition builderPosition) {
            builderPosition.setPosition((float) entityPos.x, (float) entityPos.y, (float) entityPos.z, value);
        }
    }

}
