package com.sp.destruction.client.custom.blackhole;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.FadingSoundInstance;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlackHoleDestructionClientPart1 extends ClientDestructionEvent {
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static FadingSoundInstance blackHoleAmbience;

    public BlackHoleDestructionClientPart1() {
        super(540);
    }

    @Override
    public void resetEvent() {
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

        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,
                new Keyframe(0.0, () -> {
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

                new Keyframe(220.0 / this.duration, (globalTime, localTime) -> {
                    flashTimer.setTimer((float) localTime);

                }
                ),

                new Keyframe(333.0 / this.duration, (globalTime, localTime) -> {
                    flashTimer.setTimer((float) Math.min(0.986f + localTime * 0.1f, 1.0f));
                }
                )
        ).build();
    }
}
