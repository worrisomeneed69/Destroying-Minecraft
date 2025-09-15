package com.sp.render.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.DestroyingMinecraft;
import com.sp.sounds.ModSounds;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class DestructionTitleRenderCallback implements HudRenderCallback {
    private static final DestructionTitle ORBITAL_LASER = new DestructionTitle(DestroyingMinecraft.idOf("orbital_laser.png"), 1024, 134);
    private static final DestructionTitle PLANET = new DestructionTitle(DestroyingMinecraft.idOf("planet.png"), 1003, 256);
    private static final DestructionTitle SUPERNOVA = new DestructionTitle(DestroyingMinecraft.idOf("supernova.png"), 1024, 168);
    private static final DestructionTitle BLACK_HOLE = new DestructionTitle(DestroyingMinecraft.idOf("black_hole.png"), 1024, 171);

    public static DestructionTitleAnimation ORBITAL_LASER_ANIMATION;
    public static DestructionTitleAnimation PLANET_ANIMATION;
    public static DestructionTitleAnimation SUPERNOVA_ANIMATION;
    public static DestructionTitleAnimation BLACK_HOLE_ANIMATION;

    private static long startTime = -1L;
    private static DestructionTitleAnimation currentDestructionTitle;
    private static boolean renderTitle;
    private static boolean initAnimations;

    public static void setDestructionTitle(DestructionTitleAnimation destructionTitle) {
        if(renderTitle) return;


        currentDestructionTitle = destructionTitle;
        startTime = Util.getMeasuringTimeMs();
        renderTitle = true;
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!initAnimations) {
            this.initializeAnimations(drawContext, renderTickCounter);
            initAnimations = true;
        }
        if (!renderTitle || currentDestructionTitle == null) return;



        RenderSystem.enableBlend();
        float time = (float) (Util.getMeasuringTimeMs() - startTime) / currentDestructionTitle.duration();

        currentDestructionTitle.keyframeAnimation().updateKeyframeAnimation(time);

        RenderSystem.disableBlend();

        if (time >= 1.0) {
            currentDestructionTitle.keyframeAnimation().resetAnimation();
            renderTitle = false;
            startTime = -1L;
            currentDestructionTitle = null;
        }
    }

    private void initializeAnimations(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ORBITAL_LASER_ANIMATION = new DestructionTitleAnimation(new KeyframeAnimation.KeyframeAnimationBuilder(
                200,
                new Keyframe(0.0f, () -> {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.ORBITAL_LASER_INITIALIZE, 1.0f, 1.0f));
                }, (globalTime, localTime) -> {
                    Text text = Text.literal("Initializing . . . ".substring(0, (int) Math.min(localTime*25, 18)) + "|");

                    drawContext.getMatrices().push();
                    drawContext.getMatrices().scale(2, 2, 2);

                    drawContext.getMatrices().translate(
                            (double) drawContext.getScaledWindowWidth() / 4 - (double) client.textRenderer.getWidth(text) / 2,
                            drawContext.getScaledWindowHeight()*0.05,
                            0.0);

                    drawContext.drawText(client.textRenderer, text, 0, 0, Colors.WHITE, false);
                    drawContext.getMatrices().pop();
                }),
                new Keyframe(0.35f, (globalTime, localTime) -> {
                    this.renderText(drawContext, localTime);
                })
        ).build(), ORBITAL_LASER);

        PLANET_ANIMATION = new DestructionTitleAnimation(new KeyframeAnimation.KeyframeAnimationBuilder(
                200,
                new Keyframe(0.0f, () -> {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.PLANET_INITIALIZE, 1.0f, 1.0f));
                }, (globalTime, localTime) -> {
                    Text text = Text.literal("Initializing . . . ".substring(0, (int) Math.min(localTime*25, 18)) + "|");

                    drawContext.getMatrices().push();
                    drawContext.getMatrices().scale(2, 2, 2);

                    drawContext.getMatrices().translate(
                            (double) drawContext.getScaledWindowWidth() / 4 - (double) client.textRenderer.getWidth(text) / 2,
                            drawContext.getScaledWindowHeight()*0.05,
                            0.0);

                    drawContext.drawText(client.textRenderer, text, 0, 0, Colors.WHITE, false);
                    drawContext.getMatrices().pop();
                }),
                new Keyframe(0.31f, (globalTime, localTime) -> {
                    this.renderText(drawContext, localTime);
                })
        ).build(), PLANET);

        SUPERNOVA_ANIMATION = new DestructionTitleAnimation(new KeyframeAnimation.KeyframeAnimationBuilder(
                200,
                new Keyframe(0.0f, () -> {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.SUPERNOVA_INITIALIZE, 1.0f, 1.0f));
                }, (globalTime, localTime) -> {
                    Text text = Text.literal("Initializing . . . ".substring(0, (int) Math.min(localTime*25, 18)) + "|");

                    drawContext.getMatrices().push();
                    drawContext.getMatrices().scale(2, 2, 2);

                    drawContext.getMatrices().translate(
                            (double) drawContext.getScaledWindowWidth() / 4 - (double) client.textRenderer.getWidth(text) / 2,
                            drawContext.getScaledWindowHeight()*0.05,
                            0.0);

                    drawContext.drawText(client.textRenderer, text, 0, 0, Colors.WHITE, false);
                    drawContext.getMatrices().pop();
                }),
                new Keyframe(0.47f, (globalTime, localTime) -> {
                    this.renderText(drawContext, localTime);
                })
        ).build(), SUPERNOVA);

        BLACK_HOLE_ANIMATION = new DestructionTitleAnimation(new KeyframeAnimation.KeyframeAnimationBuilder(
                500,
                new Keyframe(0.0f, () -> {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.BLACK_HOLE_INITIALIZE, 1.0f, 1.0f));
                }),
                new Keyframe(0.64f, (globalTime, localTime) -> {
                    this.renderText(drawContext, localTime);
                })
        ).build(), BLACK_HOLE, 25000L);
    }

    private void renderText(DrawContext drawContext, double localTime) {
        float alpha = (float) -Math.pow((2*localTime - 1), 6) + 1; // -(2x - 1)^6 + 1

        float height = 0.19f;
        float width = (currentDestructionTitle.title().width()*height) / currentDestructionTitle.title().height();

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(width, height, 0);
        drawContext.getMatrices().translate((double) drawContext.getScaledWindowWidth() / (2*width) - 127.5,drawContext.getScaledWindowHeight()*0.2,0);
        drawContext.setShaderColor(1, 1, 1, alpha);
        drawContext.drawTexture(currentDestructionTitle.title().texture(), 0, 0, 0, 0, 256, 255);
        drawContext.getMatrices().pop();
        drawContext.setShaderColor(1, 1, 1, 1);
    }

    public record DestructionTitleAnimation(KeyframeAnimation keyframeAnimation, DestructionTitle title, long duration) {
        public DestructionTitleAnimation(KeyframeAnimation keyframeAnimation, DestructionTitle title) {
            this(keyframeAnimation, title, 10000L);
        }
    }
    public record DestructionTitle(Identifier texture, int width, int height) {
        public DestructionTitle(Identifier texture, int width, int height) {
            this.texture = texture.withPrefixedPath("textures/gui/");
            this.width = width;
            this.height = height;
        }
    }
}