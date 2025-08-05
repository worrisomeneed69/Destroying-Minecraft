package com.sp.render;

import com.sp.DestroyingMinecraft;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

import static org.lwjgl.opengl.GL11C.GL_BACK;
import static org.lwjgl.opengl.GL11C.GL_FRONT;

/**
 * Taken from my backrooms mod
 */
public class ShadowMapRenderer {
    private static Camera currentCamera;
    private static MinecraftClient client;


    public static void renderShadowMap(Camera camera) {
        currentCamera = camera;
        client = MinecraftClient.getInstance();
        Vec3d cameraPos = camera.getPos();
        MatrixStack shadowModelView = createShadowModelView(cameraPos.x, cameraPos.y, cameraPos.z, true);
        Matrix4f shadowProjMat = createProjMat();

        AdvancedFbo shadowMap = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(DestroyingMinecraft.idOf("shadowmap"));
        if(shadowMap != null) {
            GL11.glCullFace(GL_FRONT);
            PerspectiveRenderer.render(
                    shadowMap,
                    shadowModelView.peek().getPositionMatrix(),
                    shadowProjMat,
                    new Vector3d(cameraPos.x, cameraPos.y, cameraPos.z),
                    new Quaternionf(),
                    20,
                    client.getRenderTickCounter(),
                    false
            );
            GL11.glCullFace(GL_BACK);
        }
    }


    /**
     The "do interval" bit was taken from the Iris Shadow Matrices class in order to keep the Shadows from flashing
     <a href="https://github.com/IrisShaders/Iris/blob/3fc94e8f41535feebce0bcb4235eff4a809f5eea/common/src/main/java/net/irisshaders/iris/shadows/ShadowMatrices.java">HERE</a>
     */
    public static MatrixStack createShadowModelView(double CameraX, double CameraY, double CameraZ, boolean doInterval){
        MatrixStack shadowModelView = new MatrixStack();

        shadowModelView.peek().getNormalMatrix().identity();
        shadowModelView.peek().getPositionMatrix().identity();

        shadowModelView.peek().getPositionMatrix().translate(0.0f, 0.0f, -100.0f);
        rotateShadowModelView(shadowModelView.peek().getPositionMatrix());

        if(doInterval) {
            float offsetX = (float) CameraX % 2.0f;
            float offsetY = (float) CameraY % 2.0f;
            float offsetZ = (float) CameraZ % 2.0f;

            float halfIntervalSize = 1.0f;

            offsetX -= halfIntervalSize;
            offsetY -= halfIntervalSize;
            offsetZ -= halfIntervalSize;
            shadowModelView.peek().getPositionMatrix().translate(offsetX, offsetY, offsetZ);
        }
        return shadowModelView;
    }

    //Global Light Rotation
    public static void rotateShadowModelView(Matrix4f shadowModelView) {

        switch (DestroyingMinecraftConfig.shaderType){
            case BLACK_HOLE -> {
                shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
                shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            }
            case SUPERNOVA -> {
                if(client.world != null) {
                    shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
                    shadowModelView.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(-(client.world.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F) - 90.0f));
                }
            }
            case PLANET -> {
                shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(25.0f));
                shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(20.0f));
            }
            default -> {
                shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
                shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f));
            }
        }

    }

    public static Matrix4f createProjMat(){
        return orthographicMatrix(160, 0.01f, 256.0f);
    }


    /**
     Also taken from Iris lol. Turns out their orthographic Matrix is better
     <a href="https://github.com/IrisShaders/Iris/blob/3fc94e8f41535feebce0bcb4235eff4a809f5eea/common/src/main/java/net/irisshaders/iris/shadows/ShadowMatrices.java#L13">HERE</a>
     */
    public static Matrix4f orthographicMatrix(float halfPlaneLength, float nearPlane, float farPlane) {
        return new Matrix4f(
                1.0f / halfPlaneLength, 0f, 0f, 0f,
                0f, 1.0f / halfPlaneLength, 0f, 0f,
                0f, 0f, 2.0f / (nearPlane - farPlane), 0f,
                0f, 0f, -(farPlane + nearPlane) / (farPlane - nearPlane), 1f
        );
    }

    public static void setShadowUniforms(ShaderProgram access) {
        Matrix4f viewMat = ShadowMapRenderer.createShadowModelView(currentCamera.getPos().x, currentCamera.getPos().y, currentCamera.getPos().z, true).peek().getPositionMatrix();

        BetterUniforms.setMatrix(access, "shadowViewMatrix", viewMat);
        BetterUniforms.setMatrix(access, "IShadowViewMatrix", viewMat.invert());
        BetterUniforms.setMatrix(access, "shadowProjMat", ShadowMapRenderer.createProjMat());
    }

    public static Optional<Matrix4f> getShadowViewMat() {
        if (currentCamera != null) {
            Matrix4f matrix4f = new Matrix4f();
            rotateShadowModelView(matrix4f);
            return Optional.of(matrix4f);
        }

        return Optional.empty();
    }


}
