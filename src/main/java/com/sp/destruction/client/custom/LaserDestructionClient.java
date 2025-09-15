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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LaserDestructionClient extends ClientDestructionEvent {
    public static final ShaderTimer laserLength = new ShaderTimer();
    public static final ShaderTimer cracksTime = new ShaderTimer();
    public static final ShaderTimer flashTimer = new ShaderTimer();
    private static PositionedSoundInstance laserLoop;
    private static PositionedSoundInstance crackingLoop;
    private static PositionedSoundInstance laserEnding;

    public LaserDestructionClient() {
        super(2500);
    }

    @Override
    public void resetEvent() {
        laserLength.reset();
        cracksTime.reset();
        flashTimer.reset();
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        if (laserLoop != null) {
            soundManager.stop(laserLoop);
        }
        if (crackingLoop != null) {
            soundManager.stop(crackingLoop);
        }
        if (laserEnding != null) {
            soundManager.stop(laserEnding);
        }
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "laserLength", laserLength.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "cracksTime", cracksTime.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,
                new Keyframe(0.0),

                new Keyframe(400.0 / this.duration, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_LANDING,
                                    1.0f,
                                    1.0f
                            )
                    );
                }),

                new Keyframe(478.0 / this.duration, () ->{

                }, (globalTime, localTime) -> {
                    laserLength.setTimer((float) localTime);
                }),

                new Keyframe(484.0 / this.duration, () ->{
                    laserLength.setTimer(1.0f);
                    laserLoop =  new PositionedSoundInstance(
                            ModSounds.LASER_LOOP.getId(),
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
                    soundManager.play(laserLoop);
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(
                            0.8f,
                            0.0f,
                            100,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }),

                new Keyframe(540.0 / this.duration, () ->{
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(
                            1.2f,
                            0.0f,
                            100,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }),

                new Keyframe(700.0 / this.duration, () ->{
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_CRACKING_INITIAL,
                                    1.0f,
                                    1.0f
                            )
                    );
                    crackingLoop =  new PositionedSoundInstance(
                            ModSounds.LASER_CRACKING_LOOP.getId(),
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
                    soundManager.play(crackingLoop);
                    CameraShakeInstance cameraShakeInstance = new CameraShakeInstance(
                            0.6f,
                            0.0f,
                            100,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(cameraShakeInstance);
                }, (globalTime, localTime) -> {
                    cracksTime.setTimer((float) localTime);
                }),
                new Keyframe(2100.0 / this.duration, () ->{
                    laserEnding = PositionedSoundInstance.master(
                            ModSounds.LASER_END,
                            1.0f,
                            1.0f
                    );

                    soundManager.play(laserEnding);
                }, (globalTime, localTime) -> {
                    flashTimer.setTimer((float) localTime);
                }),

                new Keyframe(2500.0 / this.duration)
        ).build();
    }
}
