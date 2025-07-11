package com.sp.render.rendertimers;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class SupernovaRenderTimer extends ExplosionRenderTimer {
    private static final ShaderTimer implodeTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static final ShaderTimer explosionTimer = new ShaderTimer();
    private static float laserLength;
    private static final List<StarPiercerEntity> starPiercers = new ArrayList<>();
    private static KeyframeAnimation supernovaAnimation;
    private static boolean initAnimation;
    private static int flashFrame = -1;
    public static int destructionDistance = Integer.MAX_VALUE;

    public SupernovaRenderTimer(int duration) {
        super(duration);
    }

    @Override
    public void updateTimer(ClientWorld clientWorld) {
        if (!initAnimation) {
            initAnimation(clientWorld);
            initAnimation = true;
        }

        if(this.enable) {
            this.progress++;

            supernovaAnimation.updateKeyframeAnimation(this.progress / this.duration);

        } else {
            supernovaAnimation.resetAnimation();
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        implodeTimer.reset();
        flashTimer.reset();
        explosionTimer.reset();
        laserLength = 0;
        starPiercers.forEach(StarPiercerEntity::reset);
        destructionDistance = Integer.MAX_VALUE;
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "supernovaTimer", implodeTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "explosionTimer", explosionTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "laserLength", laserLength);
        BetterUniforms.setInt(shaderProgram, "flashFrame", flashFrame);
    }

    private static void initAnimation(ClientWorld clientWorld) {
        supernovaAnimation = new KeyframeAnimation(
                new Keyframe(0.0f),                      //Pause

                new Keyframe((float) 15/150, () -> {     //Startup Star Piercers
                    PlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        for (StarPiercerEntity entity : clientWorld.getEntitiesByClass(
                                StarPiercerEntity.class,
                                player.getBoundingBox().expand(100),
                                Entity::isAlive)
                        ) {
                            entity.startup();
                            if(!starPiercers.contains(entity)) {
                                starPiercers.add(entity);
                            }
                        }
                    }

                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_CHARGE,
                                    1.0f,
                                    1.0f)
                    );
                }),

                new Keyframe((float) 60/150, () -> {     //Pause
                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_PAUSE,
                                    1.0f,
                                    1.0f
                            )
                    );
                }, (globalTime, localTime) -> {
                    flashFrame = flashFrame == 0 ? 1 : 0;
                }),

                new Keyframe((float) 121/300, () -> {     //Fire Star Piercers
                    SustainedCameraShakeInstance shakeInstance = new SustainedCameraShakeInstance(
                            0.8f,
                            280,
                            100,
                            Easing.LINEAR
                    );
                    CameraShakeManager.addCameraShake(shakeInstance);
                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_FIRE,
                                    1.0f,
                                    1.0f
                            )
                    );
                    flashFrame = 0;
                }, (globalTime, localTime) -> {
                    laserLength = localTime;
                }),

                new Keyframe((float) 75/150, () -> {     //Stop firing / Power down
                    for (StarPiercerEntity entity : starPiercers) {
                        entity.powerDown();
                    }
                    laserLength = 0.0f;
                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(
                                    ModSounds.LASER_POWER_DOWN,
                                    1.0f,
                                    1.0f
                            )
                    );
                }),

                new Keyframe((float) 100/150),           //Pause

                new Keyframe((float) 115/150, () -> {    //Supernova Explosion
                    MinecraftClient.getInstance().getSoundManager().play(
                            PositionedSoundInstance.master(
                                    ModSounds.SUPERNOVA_EXPLOSION,
                                    1.0f,
                                    1.0f
                            )
                    );
                }, (globalTime, localTime) -> {
                    destructionDistance = 300 - (int) (((globalTime - 0.94) / 0.035)*300);

                    implodeTimer.setPrevTimer();
                    flashTimer.setPrevTimer();
                    explosionTimer.setPrevTimer();

                    if (localTime < 0.3) {
                        //Sun implosion
                        implodeTimer.setTimer(Easing.EASE_IN_CUBIC.ease(localTime * 3.3333f));
                    } else {
                        //Flash, then fade to supernova
                        implodeTimer.maxTimer();
                        flashTimer.setTimer(Easing.EASE_IN_OUT_CUBIC.ease((localTime - 0.3f) / 0.35f));
                        explosionTimer.setTimer((localTime - 0.3f) / (0.7f));
                    }

                    if (globalTime >= 0.969) {
                        CameraShakeInstance shakeInstance = new CameraShakeInstance(
                                1f,
                                20,
                                Easing.LINEAR
                        );
                        CameraShakeManager.addCameraShake(shakeInstance);
                    }
                })

        );
    }
}