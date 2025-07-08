package com.sp.entity.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.DestroyingMinecraft;
import com.sp.entity.client.model.StarPiercerModel;
import com.sp.entity.custom.StarPiercerEntity;
import com.sp.render.materialsampler.CustomRenderLayersAndVertexFormats;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class StarPiercerEntityRenderer extends EntityRenderer<StarPiercerEntity> {
    private static final Identifier TEXTURE = DestroyingMinecraft.idOf("textures/entity/starpiercer_texture.png");
    private static final Identifier LIGHTS_TEXTURE = DestroyingMinecraft.idOf("textures/entity/starpiercer_lights_texture.png");
    private static final Identifier RENDER_TYPE = DestroyingMinecraft.idOf("entity_bloom");
    private final StarPiercerModel model;

    public StarPiercerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new StarPiercerModel(ctx.getPart(StarPiercerModel.STAR_PIERCER_MODEL_LAYER));
    }

    @Override
    public void render(StarPiercerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();
        matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(180))); // For some reason its default is upside down

        this.model.body.pitch = (float) Math.toRadians(-20);
        this.model.barrel.roll = (float) Math.toRadians(RenderSystem.getShaderGameTime()*1000000);
//        RenderLayer renderLayer = VeilRenderType.get(RENDER_TYPE, TEXTURE, LIGHTS_TEXTURE);
        RenderLayer renderLayer = CustomRenderLayersAndVertexFormats.ENTITY_BLOOM.apply(TEXTURE, LIGHTS_TEXTURE);
        this.model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private static Identifier getModelTexture() {
        return TEXTURE;
    }

    @Override
    public Identifier getTexture(StarPiercerEntity entity) {
        return TEXTURE;
    }
}
