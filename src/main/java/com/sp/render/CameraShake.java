package com.sp.render;

import foundry.veil.api.client.util.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

import java.util.Vector;

public class CameraShake {
    private static final Vector<CameraShake> allInstances = new Vector<>();
    private static PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(Random.create());
    static double zRotation = 0.0f;
    static float noiseY;
    static float amplitude = 4;
    static float shakeSpeed = 0.5f;

    private float finalTrauma;
    private final float trauma;
    private final int duration;
    private int progress;
    private final Easing easing;


    public CameraShake(float trauma, int duration, Easing easing){
        this.trauma = trauma;
        this.duration = duration;
        this.easing = easing;
        this.progress = 0;
        allInstances.add(this);
    }

    public static void totalTick(Camera camera){
        float totalTrauma = 0.0f;
        float frameDelta = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();

        if (noiseY >= 1000) {
            noiseY = 0;
        }



        for(CameraShake cameraShake : getAllInstances()){
            if(cameraShake.getTrauma() <= 0.0f){
                allInstances.remove(cameraShake);
                continue;
            }

            totalTrauma += cameraShake.getTrauma();
        }

        totalTrauma = Math.min(totalTrauma * totalTrauma, 5.0f);

        noiseY += (shakeSpeed * frameDelta);

        double pitchOffset = amplitude * totalTrauma * noiseSampler.sample(3, noiseY, 0);
        double yawOffset = amplitude * totalTrauma * noiseSampler.sample(25, noiseY, 0);
        zRotation = amplitude * totalTrauma * noiseSampler.sample(75, noiseY, 0);

        camera.setRotation((float) (camera.getYaw() + yawOffset), (float) (camera.getPitch() + pitchOffset));

    }

    public static double getzRotation(){
        return zRotation;
    }

    public void individualTick(){
        this.progress++;
        float temp = this.trauma * (1.0f - this.easing.ease((float) this.progress / this.duration));
        this.finalTrauma = Math.max(temp, 0.0f);
    }



    public float getTrauma() {
        return finalTrauma;
    }

    public static synchronized Vector<CameraShake> getAllInstances(){
        return (Vector<CameraShake>) allInstances.clone();
    }
}
