package com.sp.destruction.client.custom;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import com.sp.sounds.ModSounds;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PlanetDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer planetFallTimer = new ShaderTimer();
    private static PositionedSoundInstance ambientSound;

    public PlanetDestructionClient() {
        super(1800);
    }

    @Override
    protected void resetEvent() {
        planetFallTimer.reset();
        if (ambientSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(ambientSound);
        }
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "planetFallTimer", planetFallTimer.getTimer(tickDelta));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation(
                new Keyframe(0.0f, () -> {
                    ambientSound = new PositionedSoundInstance(
                            ModSounds.PLANET_AMBIENCE.getId(),
                            SoundCategory.AMBIENT,
                            0.8f,
                            1.0f,
                            SoundInstance.createRandom(),
                            true,
                            0,
                            SoundInstance.AttenuationType.NONE,
                            0.0,
                            0.0,
                            0.0,
                            true
                    );
                    soundManager.play(ambientSound);
                }),

                new Keyframe((float) 300/1800, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.PLANET_RUMBLE,
                                    1.0f,
                                    1.0f
                            )
                    );
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(0.8f, 120, Easing.LINEAR);
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                })
        );
    }
}
