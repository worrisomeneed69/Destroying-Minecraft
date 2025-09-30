package com.sp.render.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.DestroyingMinecraft;
import com.sp.destruction.DestructionType;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.FadingSoundInstance;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.util.timer.MsTimer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class DestructionTitleRenderCallback implements HudRenderCallback {
    private static final DestructionTitle ORBITAL_LASER = new DestructionTitle(DestroyingMinecraft.idOf("orbital_laser.png"), 1024, 134);
    private static final DestructionTitle PLANET = new DestructionTitle(DestroyingMinecraft.idOf("planet.png"), 1003, 256);
    private static final DestructionTitle SUPERNOVA = new DestructionTitle(DestroyingMinecraft.idOf("supernova.png"), 1024, 168);
    private static final DestructionTitle BLACK_HOLE = new DestructionTitle(DestroyingMinecraft.idOf("black_hole.png"), 1024, 171);

    public static DestructionTitleAnimation ORBITAL_LASER_ANIMATION;
    public static DestructionTitleAnimation PLANET_ANIMATION;
    public static DestructionTitleAnimation SUPERNOVA_ANIMATION;
    public static DestructionTitleAnimation BLACK_HOLE_ANIMATION;

    private static final MsTimer timer = new MsTimer();
    private static DestructionTitleAnimation currentDestructionTitle;
    private static boolean renderTitle;
    private static boolean initAnimations;

    public static void setDestructionTitle(DestructionType type) {
        if(renderTitle) return;

        currentDestructionTitle = switch (type) {
            case ORBITAL_LASER -> ORBITAL_LASER_ANIMATION;
            case PLANET -> PLANET_ANIMATION;
            case SUPERNOVA -> SUPERNOVA_ANIMATION;
            case BLACK_HOLE -> BLACK_HOLE_ANIMATION;
            default -> null;
        };

        if (currentDestructionTitle == null) {
            return;
        }

        timer.start();
        renderTitle = true;
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!initAnimations) {
            this.initializeAnimations(drawContext);
            initAnimations = true;
        }
        if (!renderTitle || currentDestructionTitle == null) return;



        RenderSystem.enableBlend();
        float time = (float) timer.getTime() / currentDestructionTitle.duration();
        currentDestructionTitle.keyframeAnimation().updateKeyframeAnimation(time);

        if (!MinecraftClient.getInstance().isPaused()) {
            timer.resume();
        } else {
            timer.pause();
        }

        RenderSystem.disableBlend();

        if (time >= 1.0) {
            timer.stop();
            currentDestructionTitle.keyframeAnimation().resetAnimation();
            renderTitle = false;
            currentDestructionTitle = null;
        }
    }

    private void initializeAnimations(DrawContext drawContext) {
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
                    client.getSoundManager().play(FadingSoundInstance.ambient(
                            ModSounds.BLACK_HOLE_INITIALIZE,
                            20,
                            false,
                            0,
                            1.0f,
                            1.0f
                    ));
                }, (globalTime, localTime) -> {
                    this.renderText(drawContext, localTime);
                })
        ).build(), BLACK_HOLE);
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