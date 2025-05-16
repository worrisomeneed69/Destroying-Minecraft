package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import com.sp.render.rendertimers.supernova.SupernovaRenderTimer;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class SupernovaPostShader extends PostShader {
    public static final Identifier SUPERNOVA_POST = DestroyingMinecraft.idOf("sky");
    public static final Identifier SUPERNOVA_SHADER = DestroyingMinecraft.idOf("sky/sky");

    public SupernovaPostShader() {
        super(SUPERNOVA_POST, SUPERNOVA_SHADER, new SupernovaRenderTimer(100));
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        Matrix4f matrix4f = new Matrix4f();
//        matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
//        matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees((clientWorld.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F) - 90.0f));

        matrix4f.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(25), (float) Math.toRadians(-45), (float) Math.toRadians(0)));
//        matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees(-20.0f));
//        matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));

        BetterUniforms.setMatrix(shaderProgram, "sunMat", matrix4f.invert());

        super.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
    }
}
