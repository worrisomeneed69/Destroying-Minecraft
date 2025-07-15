package com.sp.render.camerashake;

import com.sp.render.camerashake.custom.CameraShakeInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CameraShakeManager {
    private static final List<AbstractCameraShakeInstance> INSTANCES = new ArrayList<>();
    private static final PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(Random.create());

    private static float noiseY;
    private static float amplitude = 4;
    private static float shakeSpeed = 0.5f;

    private static float totalTrauma;
    private static float totalRoll;

    //I COULD lerp it to make it smoother, but it really wouldn't make much of a difference
    public static void updateCamera(Camera camera) {
        float frameDelta = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();

        if (noiseY >= 1000) {
            noiseY = 0;
        }

        noiseY += (shakeSpeed * frameDelta);

        double pitchOffset = amplitude * totalTrauma * noiseSampler.sample(3, noiseY, 0);
        double yawOffset = amplitude * totalTrauma * noiseSampler.sample(25, noiseY, 0);
        totalRoll = (float) (amplitude * totalTrauma * noiseSampler.sample(75, noiseY, 0));

        camera.setRotation((float) (camera.getYaw() + yawOffset), (float) (camera.getPitch() + pitchOffset));
    }


    public static void instancesTicks() {
        float tempTrauma = 0.0f;
        INSTANCES.removeIf(AbstractCameraShakeInstance::isFinished);
        for(AbstractCameraShakeInstance instance : INSTANCES) {
            //Max trauma
            if(tempTrauma >= 5.0f) {
                tempTrauma = 5.0f;
                break;
            }

            instance.tick();
            tempTrauma += instance.getTrauma();

        }

        totalTrauma = tempTrauma * tempTrauma;
    }


    public static float getTotalRoll() {
        return totalRoll;
    }

    public static void addCameraShake(AbstractCameraShakeInstance cameraShakeInstance) {
        INSTANCES.add(cameraShakeInstance);
    }

}
