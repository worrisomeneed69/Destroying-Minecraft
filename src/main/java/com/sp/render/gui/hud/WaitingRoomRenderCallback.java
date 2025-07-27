package com.sp.render.gui.hud;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.sounds.ModSounds;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;

public class WaitingRoomRenderCallback implements HudRenderCallback  {
    private static long startTime = -1L;
    private static boolean playedSound;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) return;
        PlayerComponent component = InitializeComponents.PLAYERS.get(player);

        if (!component.isInWaitingRoom()) {
            startTime = -1L;
            playedSound = false;
            return;
        }

        if (startTime == -1L) {
            startTime = System.currentTimeMillis();
        }

        float time = (System.currentTimeMillis() - startTime) / 5000.0f;

        if(time < 1.0) return;

        if (!playedSound) {
            client.getSoundManager().play(
                    PositionedSoundInstance.ambient(
                            ModSounds.WAITING_ROOM_HIT,
                            1.0f,
                            1.0f
                    )
            );
            playedSound = true;
        }

        int x = drawContext.getScaledWindowWidth() / 2;
        int y = drawContext.getScaledWindowHeight() / 2;

        int textWidth = client.textRenderer.getWidth("Welcome to the Waiting Room");

        float scale = 3;

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x - textWidth/(2.0 * 1/scale), y - client.textRenderer.fontHeight/(2.0 * 1/scale), 0);
        drawContext.getMatrices().scale(scale, scale, scale);
        drawContext.drawText(client.textRenderer,
                "Welcome to the Waiting Room",
                0,
                0,
                Colors.WHITE,
                false
        );
        drawContext.getMatrices().pop();

        scale = 0.95f;
        textWidth = client.textRenderer.getWidth("Sorry, our staff (SpacePotato) is a little slow -SpacePotato");
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x - textWidth/(2.0 * 1/scale), y - client.textRenderer.fontHeight/(2.0 * 1/scale), 0);
        drawContext.getMatrices().scale(scale, scale, scale);
        drawContext.drawText(client.textRenderer,
                "Sorry, our staff (SpacePotato) is a little slow -SpacePotato",
                0,
                20,
                Colors.WHITE,
                false
        );
        drawContext.getMatrices().pop();

    }
}
