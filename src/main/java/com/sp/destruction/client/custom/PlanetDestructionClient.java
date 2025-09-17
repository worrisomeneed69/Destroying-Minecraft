package com.sp.destruction.client.custom;

import com.sp.DestroyingMinecraftClient;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PlanetDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer planetFallTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static FadingSoundInstance ambientSound;

    public PlanetDestructionClient() {
        super(2400);
    }

    @Override
    public void resetEvent() {
        DestroyingMinecraftClient.destructionDistance = Integer.MAX_VALUE;
        planetFallTimer.reset();
        flashTimer.reset();
        if (ambientSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(ambientSound);
        }
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "planetFallTimer", planetFallTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,

                new Keyframe(0.0, () -> {
                    ambientSound = FadingSoundInstance.ambient(
                            ModSounds.PLANET_AMBIENCE,
                            80,
                            true,
                            0,
                            1.0f,
                            1.0f
                    );
                    soundManager.play(ambientSound);
                }),

                new Keyframe(300.0/this.duration, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.PLANET_RUMBLE,
                                    1.0f,
                                    1.0f
                            )
                    );
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(0.8f,
                            0.0f,
                            120,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }),

                new Keyframe(1800.0 /this.duration),

                new Keyframe(2170.0 /this.duration, () -> {
                    ambientSound.fadeOut();
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.PLANET_IMPACT_INITIAL,
                                    1.0f,
                                    1.0f
                            )
                    );
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(
                            0.8f,
                            0.0f,
                            40,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }, (globalTime, localTime) -> {
                    flashTimer.setTimer((float) Math.min(localTime * 2.0f, 1.0f));
                }),

                new Keyframe(2200.0f /this.duration, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.PLANET_IMPACT,
                                    1.0f,
                                    1.0f
                            )
                    );
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(
                            0.8f,
                            4.5f,
                            220,
                            Easing.EASE_IN_QUINT
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }, (globalTime, localTime) -> {
                    flashTimer.setTimer(1.0f);
                }),

                new Keyframe(2300.0f / this.duration, ((globalTime, localTime) -> {
                    DestroyingMinecraftClient.destructionDistance = (int) ((1.0f - localTime) * 300);
                }))
        ).globalAction((globalTime, localTime) -> {
            planetFallTimer.setTimer((float) globalTime);
        }).build();
    }
}
