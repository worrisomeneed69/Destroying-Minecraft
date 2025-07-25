package com.sp.render.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.mixininterfaces.PlayZoneEntity;
import com.sp.sounds.ModSounds;
import com.sp.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class PlayZoneWarningRenderCallback implements HudRenderCallback {
    private static final Identifier warningImage = DestroyingMinecraft.idOf("textures/gui/warning.png");
    private static long startTime;
    private static PositionedSoundInstance countdown;


    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        SoundManager soundManager = client.getSoundManager();
        if (player == null) return;
        PlayerComponent component = InitializeComponents.PLAYERS.get(player);


        if (component.isInsideAPlayZone()) {
            startTime = 0L;
            if (soundManager.isPlaying(countdown)) {
                soundManager.stop(countdown);
            }
            return;
        }

        if (startTime == 0L) {
            countdown = PositionedSoundInstance.ambient(
                    ModSounds.COUNT_DOWN,
                    1.0f,
                    0.5f
            );
            soundManager.play(countdown);
            startTime = System.currentTimeMillis();
        }

        RenderSystem.enableBlend();

        float alphaFade = Math.min((System.currentTimeMillis() - startTime) / 200.0f, 1.0f);
        float alpha = (float) (Math.sin(RenderSystem.getShaderGameTime() * 8000) * 0.5 + 0.5) * 0.5f + 0.4f;

        drawContext.fill(
                0,
                0,
                drawContext.getScaledWindowWidth(),
                drawContext.getScaledWindowHeight(),
                RenderUtil.getArgb((int) (alphaFade * 100), 255, 50, 50)
        );

        RenderSystem.enableBlend(); // For some reason I need to re-enable it??
        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, alpha - (1.0f - alphaFade));
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate((float) drawContext.getScaledWindowWidth() / 2, (float) drawContext.getScaledWindowHeight() / 2, 0);
        drawContext.getMatrices().scale(0.5f, 0.5f, 0.5f);


        drawContext.drawTexture(warningImage, -128, -256, 0, 0, 256, 255);

        drawContext.getMatrices().scale(5.0f, 5.0f, 5.0f);
        drawContext.drawCenteredTextWithShadow(client.textRenderer, "Return to the Play Zone", 0, client.textRenderer.fontHeight / 2, Colors.WHITE);

        long timeLeft = Math.max(component.getDeathTime() - System.currentTimeMillis(), 0L);
        int seconds = Math.max((int) Math.floor((double) timeLeft / 1000), 0);
        String milliSecondsString;
        long milliSeconds = timeLeft%1000;

        if(milliSeconds > 100L) {
            milliSecondsString = String.valueOf(milliSeconds);
        } else if (milliSeconds > 10L) {
            milliSecondsString = "0" + milliSeconds;
        } else {
            milliSecondsString = "00" + milliSeconds;
        }

        drawContext.drawCenteredTextWithShadow(client.textRenderer, "00:0" + seconds + ":" + milliSecondsString, 0, client.textRenderer.fontHeight * 2, Colors.WHITE);

        drawContext.getMatrices().pop();
        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);


        RenderSystem.disableBlend();
    }

}
