package com.sp.entity.client.renderer;

import com.sp.DestroyingMinecraft;
import com.sp.entity.client.model.StarPiercerModel;
import com.sp.entity.custom.StarPiercerEntity;
import com.sp.render.CustomRenderLayersAndVertexFormats;
import com.sp.render.ShadowMapRenderer;
import com.sp.util.BetterUniforms;
import com.sp.util.timer.MsTimer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
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
    private static final Identifier STAR_PIERCER_SHADER = DestroyingMinecraft.idOf("star_piercer/star_piercer");
    private static final MsTimer bloomTimer = new MsTimer();
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

        ShaderProgram shader = VeilRenderSystem.setShader(STAR_PIERCER_SHADER);
        if (shader == null) return;
        float lastFrameDuration = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();
        float bloomTime = 0;
        if (!MinecraftClient.getInstance().isPaused()) {
            bloomTimer.resume();

            if (entity.isStartingUp()) {
                bloomTimer.start();
                bloomTime = (float) (bloomTimer.getTime() - 34000L) / 10000;  // 34 second delay before glowing, 10 second duration
                this.speed += 0.000202222f * lastFrameDuration;
                this.speed = Math.min(this.speed, 2f);
                this.model.barrel.roll += this.speed * lastFrameDuration;
            } else if (entity.isPoweringDown()) {
                bloomTime = 1.0f - (float) (bloomTimer.getTime() - 60000L) / 20000;
                this.speed -= 0.000052222f * lastFrameDuration;
                this.speed = Math.max(this.speed, 0.00f);
                this.model.barrel.roll += this.speed * lastFrameDuration;
            } else {
                this.speed = 0.0f;
                bloomTimer.stop();
            }
        } else {
            bloomTimer.pause();
        }

        BetterUniforms.setFloat(shader, "bloomTime", Math.clamp(bloomTime, 0.0f, 1.0f));

        RenderLayer renderLayer = CustomRenderLayersAndVertexFormats.ENTITY_BLOOM.apply(TEXTURE, LIGHTS_TEXTURE);
        this.model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    @Override
    public boolean shouldRender(StarPiercerEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public Identifier getTexture(StarPiercerEntity entity) {
        return TEXTURE;
    }
}
