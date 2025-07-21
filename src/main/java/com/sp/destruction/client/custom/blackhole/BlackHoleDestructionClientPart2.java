package com.sp.destruction.client.custom.blackhole;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import com.sp.render.camerashake.custom.SustainedCameraShakeInstance;
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
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlackHoleDestructionClientPart2 extends ClientDestructionEvent {
    private PositionedSoundInstance destructionSoundInstance;

    public BlackHoleDestructionClientPart2() {
        super(3400);
    }

    @Override
    protected void resetEvent() {
        if (destructionSoundInstance != null) {
            MinecraftClient.getInstance().getSoundManager().stop(destructionSoundInstance);
        }
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation(
                new Keyframe(0.0, () -> {
                    soundManager.play(PositionedSoundInstance.master(ModSounds.SNAP_SNAP, 1.0f, 1.0f));
                }),

                new Keyframe(22.0 / this.duration, () -> {
                    soundManager.play(PositionedSoundInstance.master(ModSounds.SNAP_SNAP_RUMBLE, 1.0f, 1.0f));
                    SustainedCameraShakeInstance cameraShakeInstance = new SustainedCameraShakeInstance(0.5f, 100, 20, Easing.LINEAR);
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }),

                new Keyframe(135.0 / this.duration, () -> {
                    soundManager.play(PositionedSoundInstance.master(ModSounds.SNAP_SNAP_BREAK_OFF, 1.0f, 1.0f));
                    destructionSoundInstance = new PositionedSoundInstance(
                            ModSounds.BLACK_HOLE_DESTRUCTION_AMBIENCE.getId(),
                            SoundCategory.AMBIENT,
                            1.0f,
                            1.0f,
                            SoundInstance.createRandom(),
                            true,
                            0,
                            SoundInstance.AttenuationType.LINEAR,
                            0.0f,
                            0.0f,
                            0.0f,
                            true
                    );
                    soundManager.play(destructionSoundInstance);
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(0.9f, 0.0f, 40, Easing.LINEAR);
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                })
        );
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {

    }
}
