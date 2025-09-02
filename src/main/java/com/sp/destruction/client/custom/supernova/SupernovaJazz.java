package com.sp.destruction.client.custom.supernova;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.render.BlackScreenManager;
import com.sp.sounds.ModSounds;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SupernovaJazz extends ClientDestructionEvent {
    public SupernovaJazz() {
        super(400);
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {

    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation(
                this.duration,
                new Keyframe(0.0, () -> {
                    BlackScreenManager.setBlackScreen(true);
                    soundManager.play(PositionedSoundInstance.ambient(
                            ModSounds.SUPERNOVA_JAZZ,
                            1.0f,
                            1.0f
                        )
                    );
                }),

                new Keyframe(362.0 / this.duration, () -> {
                    BlackScreenManager.setBlackScreen(false);
                })
        );
    }
}
