package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.render.BlackScreenManager;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import com.sp.util.MathUtil;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PostProcessingPostShader extends PostShader {
    public static final Identifier POST = DestroyingMinecraft.idOf("post");
    public static final Identifier SHADER = DestroyingMinecraft.idOf("post/post");
    public static final Identifier[] BLUR_IDENTIFIERS = {
            DestroyingMinecraft.idOf("bloom/blur/horizontal"),
            DestroyingMinecraft.idOf("bloom/blur/vertical")
    };
    float smoothDepth;

    public PostProcessingPostShader() {
        super(POST, SHADER);
    }

    @Override
    public void setUniforms(PostPipeline.Context context, float tickDelta, MinecraftClient client, World clientWorld) {
        super.setUniforms(context, tickDelta, client, clientWorld);

        for(Identifier identifier : BLUR_IDENTIFIERS) {
            ShaderProgram shaderProgram = context.getShader(identifier);
            if (shaderProgram != null) {
                BetterUniforms.setFloat(shaderProgram, "blurStrength", DestroyingMinecraftConfig.blurStrength);
                BetterUniforms.setFloat(shaderProgram, "xLimit", DestroyingMinecraftConfig.enableDepthOfField ? 1.0f : -0.1f);
            }
        }
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        if (client.player == null) return;
        PlayerComponent component = InitializeComponents.PLAYERS.get(client.player);

        float farPlane = 100;

        Vec3d vec3d = client.player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = client.player.getRotationVec(tickDelta).normalize();
        Vec3d vec3d3 = vec3d.add(vec3d2.x * farPlane, vec3d2.y * farPlane, vec3d2.z * farPlane);
        HitResult hitResult = client.player.getWorld()
                .raycast(
                        new RaycastContext(
                                vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, client.player
                        )
                );

        Box box = client.player.getBoundingBox().stretch(vec3d2).expand(farPlane);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(client.player, vec3d, vec3d3, box, entity -> true, farPlane);

        Vec3d closestDistance;
        if (entityHitResult == null) {
            closestDistance = hitResult.getPos();
        } else if (entityHitResult.getPos().length() > hitResult.getPos().length()) {
            closestDistance = hitResult.getPos();
        } else {
            closestDistance = entityHitResult.getPos();
        }

        float depth = (float) client.getCameraEntity().getEyePos().distanceTo(closestDistance) / farPlane;

        //Smooths down the Depth at the crosshair so it gives a "not so instant" autofocus effect
        this.smoothDepth = MathUtil.Lerp(this.smoothDepth, depth, DestroyingMinecraftConfig.autoFocusTime, MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration());
        BetterUniforms.setFloat(shaderProgram, "centerDepth", this.smoothDepth);

        BetterUniforms.setInt(shaderProgram, "enabledDepthOfField", DestroyingMinecraftConfig.enableDepthOfField ? 1 : 0);
        BetterUniforms.setInt(shaderProgram, "enabledBlackScreen", BlackScreenManager.isBlackScreen() || component.isInWaitingRoom() ? 1 : 0);

        BetterUniforms.setFloat(shaderProgram, "glitchTime", Math.min(component.getGlitchTime() / 100.0f, 1.0f));
    }
}
