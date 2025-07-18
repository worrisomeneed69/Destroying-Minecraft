package com.sp.destruction.client.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.FadingSoundInstance;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlackHoleDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static FadingSoundInstance blackHoleAmbience;

    public BlackHoleDestructionClient() {
        super(1000);
    }

    @Override
    protected void resetEvent() {
        flashTimer.reset();
        if (blackHoleAmbience != null) {
            MinecraftClient.getInstance().getSoundManager().stop(blackHoleAmbience);
        }
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "flashTimer", Math.min(flashTimer.getTimer(tickDelta) + 0.01f, 1.0f));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation(
                new Keyframe(0.0f, () -> {
                    blackHoleAmbience = FadingSoundInstance.ambient(
                            ModSounds.BLACK_HOLE_AMBIENCE,
                            100,
                            true,
                            0,
                            1.0f,
                            1.0f
                    );
                    soundManager.play(blackHoleAmbience);
                }),

                new Keyframe(280.0f / this.duration, (globalTime, localTime) -> {
                        flashTimer.setPrevTimer();
                        flashTimer.setTimer(localTime);
                    }
                ),

                new Keyframe(333.0f / this.duration, (globalTime, localTime) -> {
                        flashTimer.setPrevTimer();
                        flashTimer.setTimer(Math.min(0.986f + localTime * 0.1f, 1.0f));
                    }
                )
        );
    }
}
