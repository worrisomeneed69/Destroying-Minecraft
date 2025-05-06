package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.mixin.WorldRendererAccessor;
import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.compat.SodiumCompat;
import foundry.veil.ext.RenderTargetExtension;
import foundry.veil.impl.client.render.perspective.IrisPipelineAccess;
import foundry.veil.impl.client.render.perspective.LevelPerspectiveCamera;
import foundry.veil.mixin.perspective.accessor.GameRendererAccessor;
import foundry.veil.mixin.perspective.accessor.LevelRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.concurrent.atomic.AtomicInteger;


public class PerspectiveRenderer {

    private static final LevelPerspectiveCamera CAMERA = new LevelPerspectiveCamera();
    private static final Matrix4f TRANSFORM = new Matrix4f();
    private static final CameraMatrices BACKUP_CAMERA_MATRICES = new CameraMatrices();
    private static final AtomicInteger ID = new AtomicInteger();

    private static final Matrix4f BACKUP_PROJECTION = new Matrix4f();
    private static final Vector3f BACKUP_LIGHT0_POSITION = new Vector3f();
    private static final Vector3f BACKUP_LIGHT1_POSITION = new Vector3f();

    private static boolean renderingPerspective = false;

    private PerspectiveRenderer() {
    }


    public static AdvancedFbo render(AdvancedFbo framebuffer, Matrix4fc modelView, Matrix4fc projection, Vector3dc cameraPosition, Quaternionfc cameraOrientation, float renderDistance, RenderTickCounter deltaTracker, boolean drawLights) {
        return render(framebuffer, MinecraftClient.getInstance().cameraEntity, modelView, projection, cameraPosition, cameraOrientation, renderDistance, deltaTracker, drawLights);
    }

    /**
     * Renders the level from another POV. Automatically prevents circular render references.
     *
     * @param framebuffer       The framebuffer to draw into
     * @param cameraEntity      The entity to draw the camera in relation to. If unsure use {@link #render(AdvancedFbo, Matrix4fc, Matrix4fc, Vector3dc, Quaternionfc, float, RenderTickCounter, boolean)}
     * @param modelView         The base modelview matrix
     * @param projection        The projection matrix
     * @param cameraPosition    The position of the camera
     * @param cameraOrientation The orientation of the camera
     * @param renderDistance    The chunk render distance
     * @param deltaTracker      The delta tracker instance
     * @param drawLights        Whether to draw lights to the scene after
     * @return The full framebuffer including dynamic buffers. This framebuffer is owned by the render system
     */
    public static AdvancedFbo render(AdvancedFbo framebuffer, @Nullable Entity cameraEntity, Matrix4fc modelView, Matrix4fc projection, Vector3dc cameraPosition, Quaternionfc cameraOrientation, float renderDistance, RenderTickCounter deltaTracker, boolean drawLights) {
        if (renderingPerspective) {
            return framebuffer;
        }

        // Finish anything previously being rendered for safety
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        bufferSource.draw();

        final MinecraftClient minecraft = MinecraftClient.getInstance();
        final GameRenderer gameRenderer = minecraft.gameRenderer;
        final WorldRenderer levelRenderer = minecraft.worldRenderer;
        final WorldRendererAccessor accessor1 = (WorldRendererAccessor) levelRenderer;
        final LevelRendererAccessor levelRendererAccessor = (LevelRendererAccessor) levelRenderer;
        final Window window = minecraft.getWindow();
        final GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        final RenderTargetExtension renderTargetExtension = (RenderTargetExtension) minecraft.getFramebuffer();
        final MatrixStack poseStack = new MatrixStack();

        CAMERA.setup(cameraPosition, cameraEntity, minecraft.world, cameraOrientation, renderDistance);

        poseStack.multiplyPositionMatrix(TRANSFORM.set(modelView));
        poseStack.multiply(CAMERA.getRotation());

        float backupRenderDistance = gameRenderer.getViewDistance();
        accessor.setRenderDistance(renderDistance * 16.0F);

        float backupFogStart = RenderSystem.getShaderFogStart();
        float backupFogEnd = RenderSystem.getShaderFogEnd();
        FogShape backupFogShape = RenderSystem.getShaderFogShape();

        int backupWidth = window.getFramebufferWidth();
        int backupHeight = window.getFramebufferHeight();
//        window.setFramebufferWidth(framebuffer.getWidth());
//        window.setFramebufferHeight(framebuffer.getHeight());

        final Object backupPipeline = IrisPipelineAccess.getPipeline(levelRenderer);

        final Object backupRenderLists;
        final Object backupTaskLists;
        if (SodiumCompat.isLoaded()) {
            backupRenderLists = SodiumCompat.INSTANCE.getSortedRenderLists();
            backupTaskLists = SodiumCompat.INSTANCE.getTaskLists();
            ID.getAndIncrement();
        } else {
            backupRenderLists = null;
            backupTaskLists = null;
        }

        BACKUP_PROJECTION.set(RenderSystem.getProjectionMatrix());
        gameRenderer.loadProjectionMatrix(TRANSFORM.set(projection));
        BACKUP_LIGHT0_POSITION.set(VeilRenderSystem.getLight0Direction());
        BACKUP_LIGHT1_POSITION.set(VeilRenderSystem.getLight1Direction());

        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.identity();
        RenderSystem.applyModelViewMatrix();

        HitResult backupHitResult = minecraft.crosshairTarget;
        Entity backupCrosshairPickEntity = minecraft.targetedEntity;

        renderingPerspective = true;
        AdvancedFbo drawFbo = VeilRenderSystem.renderer().getDynamicBufferManger().getDynamicFbo(framebuffer);
        drawFbo.bind(true);
        renderTargetExtension.veil$setWrapper(drawFbo);

        Frustum backupFrustum = levelRendererAccessor.getCullingFrustum();

        CameraMatrices matrices = VeilRenderSystem.renderer().getCameraMatrices();
        matrices.backup(BACKUP_CAMERA_MATRICES);

        try {
            levelRenderer.setupFrustum(new Vec3d(cameraPosition.x(), cameraPosition.y(), cameraPosition.z()), poseStack.peek().getPositionMatrix(), TRANSFORM);

//            accessor1.invokeSetupTerrain(CAMERA, levelRendererAccessor.getCullingFrustum(), false, false);
            accessor1.invokeRenderLayer(RenderLayer.getCutout(), cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), poseStack.peek().getPositionMatrix(), TRANSFORM);
            accessor1.invokeRenderLayer(RenderLayer.getCutoutMipped(), cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), poseStack.peek().getPositionMatrix(), TRANSFORM);
            accessor1.invokeRenderLayer(RenderLayer.getSolid(), cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), poseStack.peek().getPositionMatrix(), TRANSFORM);

            if(minecraft.world != null) {
                VertexConsumerProvider.Immediate immediate = accessor1.getBufferBuilders().getEntityVertexConsumers();

                for(Entity entity : minecraft.world.getEntities()){
                    if(accessor1.getEntityRenderDispatcher().shouldRender(entity, levelRendererAccessor.getCullingFrustum(), cameraPosition.x(), cameraPosition.y(), cameraPosition.z()) && !entity.isSpectator()){
                        accessor1.invokeRenderEntity(entity, cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), minecraft.getRenderTickCounter().getTickDelta(true), poseStack, immediate);
                    }
                }

                immediate.draw();

            }
            // Make sure all buffers have been finished
            bufferSource.draw();
            levelRenderer.drawEntityOutlinesFramebuffer();

            // Draw lights
            if (drawLights) {
                Profiler profiler = MinecraftClient.getInstance().getProfiler();
                if (VeilRenderSystem.drawLights(profiler, VeilRenderSystem.getCullingFrustum())) {
                    VeilRenderSystem.compositeLights(profiler);
                } else {
                    AdvancedFbo.unbind();
                }
            }
        } finally {
            matrices.restore(BACKUP_CAMERA_MATRICES);

            levelRendererAccessor.setCullingFrustum(backupFrustum);

            renderTargetExtension.veil$setWrapper(null);
            AdvancedFbo.unbind();
            renderingPerspective = false;

            minecraft.targetedEntity = backupCrosshairPickEntity;
            minecraft.crosshairTarget = backupHitResult;

            matrix4fstack.popMatrix();
            RenderSystem.applyModelViewMatrix();

            RenderSystem.setShaderLights(BACKUP_LIGHT0_POSITION, BACKUP_LIGHT1_POSITION);
            gameRenderer.loadProjectionMatrix(BACKUP_PROJECTION);

            IrisPipelineAccess.setPipeline(levelRenderer, backupPipeline);

            if (SodiumCompat.isLoaded()) {
                SodiumCompat.INSTANCE.setSortedRenderLists(backupRenderLists);
                SodiumCompat.INSTANCE.setTaskList(backupTaskLists);
            }

            RenderSystem.setShaderFogStart(backupFogStart);
            RenderSystem.setShaderFogEnd(backupFogEnd);
            RenderSystem.setShaderFogShape(backupFogShape);

//            window.setFramebufferWidth(backupWidth);
//            window.setFramebufferHeight(backupHeight);

            accessor.setRenderDistance(backupRenderDistance);

            // Reset the renderers to what they used to be
            Camera mainCamera = gameRenderer.getCamera();
            minecraft.getBlockEntityRenderDispatcher().configure(minecraft.world, mainCamera, minecraft.crosshairTarget);
            minecraft.getEntityRenderDispatcher().configure(minecraft.world, mainCamera, minecraft.targetedEntity);
        }
        return drawFbo;
    }



    /**
     * @return Whether a perspective is being rendered
     */
    public static boolean isRenderingPerspective() {
        return renderingPerspective;
    }

}
