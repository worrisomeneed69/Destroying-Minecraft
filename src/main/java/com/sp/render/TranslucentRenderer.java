package com.sp.render;

import com.sp.DestroyingMinecraft;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.ext.VeilDebug;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.ext.RenderTargetExtension;
import foundry.veil.impl.client.render.dynamicbuffer.DynamicBufferManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class TranslucentRenderer {
    private static final Identifier TRANSLUCENT = DestroyingMinecraft.idOf("translucent");

    private static boolean printedError;
    private static AdvancedFbo translucent;

    public static void bind(int mask) {
        VeilDebug.get().pushDebugGroup("Destroying-Minecraft Translucent");

        AdvancedFbo mainRenderTarget = AdvancedFbo.getMainFramebuffer();
        int w = mainRenderTarget.getWidth();
        int h = mainRenderTarget.getHeight();
        int framebufferTexture = mainRenderTarget.getColorTextureAttachment(0).getGlId();
        if (translucent == null || translucent.getWidth() != w || translucent.getHeight() != h) {
            free();
            translucent = AdvancedFbo.withSize(w, h)
                    .addColorTextureWrapper(framebufferTexture)
                    .setFormat(FramebufferAttachmentDefinition.Format.DEPTH_COMPONENT)
                    .setDepthTextureBuffer()
                    .setDebugLabel("Destroying-Minecraft Translucent")
                    .build(true);
        }

        DynamicBufferManager dynamicBufferManager = VeilRenderSystem.renderer().getDynamicBufferManger();
        dynamicBufferManager.setEnabled(true);
        AdvancedFbo fbo = dynamicBufferManager.getDynamicFbo(translucent);
        dynamicBufferManager.setEnabled(false);

        VeilRenderSystem.renderer().getFramebufferManager().setFramebuffer(TRANSLUCENT, fbo);
        fbo.clear(mask);
        fbo.toRenderTarget().copyDepthFrom(mainRenderTarget.toRenderTarget());
        fbo.bind(false);

        ((RenderTargetExtension) MinecraftClient.getInstance().getFramebuffer()).veil$setWrapper(fbo);
    }

    public static void unbind() {
        // TODO update projection/modelview matrix
        Profiler profiler = MinecraftClient.getInstance().getProfiler();
        boolean rendered = VeilRenderSystem.drawLights(profiler, VeilRenderSystem.getCullingFrustum());
        ((RenderTargetExtension) MinecraftClient.getInstance().getFramebuffer()).veil$setWrapper(null);

        if (rendered) {
            VeilRenderSystem.compositeLights(profiler);
        }

        VeilRenderer renderer = VeilRenderSystem.renderer();
        PostProcessingManager postProcessingManager = renderer.getPostProcessingManager();

        PostPipeline pipeline = postProcessingManager.getPipeline(TRANSLUCENT);
        if (pipeline == null) {
            if (!printedError) {
                Veil.LOGGER.error("Failed to apply translucent pipeline");
                printedError = true;
            }
        } else {
            postProcessingManager.runPipeline(pipeline, false);
        }

        AdvancedFbo.unbind();
        VeilDebug.get().popDebugGroup();
    }

    public static void free() {
        if (translucent != null) {
            VeilRenderSystem.renderer().getFramebufferManager().removeFramebuffer(TRANSLUCENT);
            translucent.free();
            translucent = null;
        }
        printedError = false;
    }
}
