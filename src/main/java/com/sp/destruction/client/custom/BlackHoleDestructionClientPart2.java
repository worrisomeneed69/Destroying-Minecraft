package com.sp.destruction.client.custom;

import com.sp.destruction.DestructionType;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import com.sp.render.camerashake.custom.SustainedCameraShakeInstance;
import com.sp.render.postshaders.PostShaders;
import com.sp.render.postshaders.custom.BlackHolePostShader;
import com.sp.sounds.ModSounds;
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
        super(DestructionType.BLACK_HOLE, PostShaders.BLACK_HOLE, 3400);
    }

    @Override
    public void resetEvent() {
        if (destructionSoundInstance != null) {
            MinecraftClient.getInstance().getSoundManager().stop(destructionSoundInstance);
        }
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
//        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

//        return new KeyframeAnimation.KeyframeAnimationBuilder(
//                this.duration,
//
//                new Keyframe(0.0),
//
//                new Keyframe(22.0 / this.duration, () -> {
//                    soundManager.play(PositionedSoundInstance.master(ModSounds.SNAP_SNAP_RUMBLE, 1.0f, 1.0f));
//                    SustainedCameraShakeInstance cameraShakeInstance = new SustainedCameraShakeInstance(0.5f, 100, 20, Easing.LINEAR);
//                    CameraShakeManager.addCameraShake(cameraShakeInstance);
//                }),
//
//                new Keyframe(135.0 / this.duration, () -> {
//                    soundManager.play(PositionedSoundInstance.master(ModSounds.SNAP_SNAP_BREAK_OFF, 1.0f, 1.0f));
//                    destructionSoundInstance = new PositionedSoundInstance(
//                            ModSounds.BLACK_HOLE_DESTRUCTION_AMBIENCE.getId(),
//                            SoundCategory.AMBIENT,
//                            1.0f,
//                            1.0f,
//                            SoundInstance.createRandom(),
//                            true,
//                            0,
//                            SoundInstance.AttenuationType.LINEAR,
//                            0.0f,
//                            0.0f,
//                            0.0f,
//                            true
//                    );
//                    soundManager.play(destructionSoundInstance);
//                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(0.9f, 0.0f, 40, Easing.LINEAR);
//                    CameraShakeManager.addCameraShake(cameraShakeInstance);
//                })
//        ).build();
        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,
                new Keyframe(0.0f)
        ).build();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {

    }
}
