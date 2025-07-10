package com.sp.entity.client.renderer;

import com.sp.DestroyingMinecraft;
import com.sp.entity.client.model.StarPiercerModel;
import com.sp.entity.custom.StarPiercerEntity;
import com.sp.render.CustomRenderLayersAndVertexFormats;
import com.sp.render.ShadowMapRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class StarPiercerEntityRenderer extends EntityRenderer<StarPiercerEntity> {
    private static final Identifier TEXTURE = DestroyingMinecraft.idOf("textures/entity/starpiercer_texture.png");
    private static final Identifier LIGHTS_TEXTURE = DestroyingMinecraft.idOf("textures/entity/starpiercer_lights_texture.png");
    private final StarPiercerModel model;
    private float speed = 0;

    public StarPiercerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new StarPiercerModel(ctx.getPart(StarPiercerModel.STAR_PIERCER_MODEL_LAYER));
    }

    @Override
    public void render(StarPiercerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();
        matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(180))); // For some reason its default is upside down

        Optional<Matrix4f> sunMat = ShadowMapRenderer.getShadowViewMat();


        if (sunMat.isPresent()) {
            Matrix3f matrix3f = new Matrix3f(sunMat.get().invert());

            Vector3f angles = new Vector3f(0, 0, 1).mul(matrix3f).normalize();
            float pitch = (float) Math.atan2(angles.y, angles.x);
            float yaw2 = (float) Math.atan2(angles.z, angles.x);

            this.model.body.pitch = -pitch;
            this.model.starpiercer.yaw = yaw2 + (float) Math.toRadians(90);
        }

        if (!MinecraftClient.getInstance().isPaused()) {
            if (entity.isStartingUp()) {
//                this.speed = 0;
//                this.model.barrel.roll = 0;

                this.speed += 0.000032222f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();
                this.speed = Math.min(this.speed, 0.07f);
                this.model.barrel.roll += this.speed;
            } else if (entity.isPoweringDown()) {
                this.speed -= 0.000032222f * MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();
                this.speed = Math.max(this.speed, 0.00f);
//                System.out.println(this.speed);
                this.model.barrel.roll += this.speed;
            } else {
                this.speed = 0.0f;
            }
        }

        RenderLayer renderLayer = CustomRenderLayersAndVertexFormats.ENTITY_BLOOM.apply(TEXTURE, LIGHTS_TEXTURE);
        this.model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(StarPiercerEntity entity) {
        return TEXTURE;
    }
}
