package com.sp.destruction.client.custom.supernova;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.entity.custom.StarPiercerEntity;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import com.sp.render.camerashake.custom.SustainedCameraShakeInstance;
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
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SupernovaDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer implodeTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static final ShaderTimer explosionTimer = new ShaderTimer();
    private static float laserLength;
    private static final List<StarPiercerEntity> starPiercers = new ArrayList<>();
    private static int flashFrame = -1;
    public static int destructionDistance = Integer.MAX_VALUE;

    public SupernovaDestructionClient() {
        super(3000);
    }

    @Override
    public void resetEvent() {
        implodeTimer.reset();
        flashTimer.reset();
        explosionTimer.reset();
        laserLength = 0;
        starPiercers.forEach(StarPiercerEntity::reset);
        destructionDistance = Integer.MAX_VALUE;
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "supernovaTimer", implodeTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "explosionTimer", explosionTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "laserLength", laserLength);
        BetterUniforms.setInt(shaderProgram, "flashFrame", flashFrame);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

        return new KeyframeAnimation(
                //*Pause
                new Keyframe(0.0f),

                //*Startup Star Piercers
                new Keyframe(15.0/150, () -> {
                    PlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        for (StarPiercerEntity entity : world.getEntitiesByClass(
                                StarPiercerEntity.class,
                                player.getBoundingBox().expand(1000),
                                Entity::isAlive)
                        ) {
                            entity.startup();
                            if(!starPiercers.contains(entity)) {
                                starPiercers.add(entity);
                            }
                        }
                    }

                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_CHARGE,
                                    1.0f,
                                    1.0f)
                    );
                }),

                //*Pause
                new Keyframe(60.0/150, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_PAUSE,
                                    1.0f,
                                    1.0f
                            )
                    );
                }, (globalTime, localTime) -> {
                    flashFrame = flashFrame == 0 ? 1 : 0;
                }),

                //*Fire Star Piercers
                new Keyframe(121.0/300, () -> {
                    SustainedCameraShakeInstance shakeInstance = new SustainedCameraShakeInstance(
                            0.8f,
                            280,
                            100,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(shakeInstance);
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_FIRE,
                                    1.0f,
                                    1.0f
                            )
                    );
                    flashFrame = 0;
                }, (globalTime, localTime) -> {
                    laserLength = (float) localTime;
                }),

                //*Stop firing / Power down
                new Keyframe(75.0/150, () -> {
                    for (StarPiercerEntity entity : starPiercers) {
                        entity.powerDown();
                    }
                    laserLength = 0.0f;
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_POWER_DOWN,
                                    1.0f,
                                    1.0f
                            )
                    );
                }),

                //*Pause
                new Keyframe(100.0/150),

                //*Supernova Explosion
                new Keyframe(115.0/150, () -> {
                    soundManager.play(
                            PositionedSoundInstance.master(
                                    ModSounds.SUPERNOVA_EXPLOSION,
                                    1.0f,
                                    1.0f
                            )
                    );
                }, (globalTime, localTime) -> {
                    destructionDistance = 300 - (int) (((globalTime - 0.94) / 0.035)*300);


                    if (localTime < 0.3) {
                        //Sun implosion
                        implodeTimer.setTimer(Easing.EASE_IN_CUBIC.ease((float) (localTime * 3.3333f)));
                    } else {
                        //Flash, then fade to supernova
                        implodeTimer.maxTimer();
                        flashTimer.setTimer(Math.clamp(Easing.EASE_IN_OUT_CUBIC.ease((float) ((localTime - 0.3f) / 0.35f)), 0.0f, 1.0f));
                        explosionTimer.setTimer((float) ((localTime - 0.3f) / (0.7f)));
                    }

                    if (globalTime >= 0.969) {
                        CameraShakeInstance shakeInstance = new CameraShakeInstance(
                                1.0f,
                                0.0f,
                                20,
                                Easing.LINEAR
                        );
                        CameraShakeManager.addCameraShake(shakeInstance);
                    }
                })

        );
    }
}
