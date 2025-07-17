package com.sp.sounds.instances;

import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class FadingSoundInstance extends MovingSoundInstance implements TickableSoundInstance {
    private final int fadeTime;
    private final float targetVolume;
    private int currentTime;
    private boolean shouldFadeOut;

    public static FadingSoundInstance ambient(
            SoundEvent soundEvent,
            int fadeTime,
            boolean repeat,
            int repeatDelay,
            float volume,
            float pitch
    ) {
        return new FadingSoundInstance(soundEvent, SoundCategory.AMBIENT, 0.0, 0.0, 0.0, fadeTime, repeat, repeatDelay, true, volume, pitch);
    }

    public FadingSoundInstance(
            SoundEvent soundEvent,
            SoundCategory soundCategory,
            double x,
            double y,
            double z,
            int fadeTime,
            boolean repeat,
            int repeatDelay,
            boolean relative,
            float volume,
            float pitch
    ) {
        super(soundEvent, soundCategory, SoundInstance.createRandom());
        this.x = x;
        this.y = y;
        this.z = z;
        this.fadeTime = fadeTime;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.relative = relative;
        this.targetVolume = volume;
        this.volume = 0.1f;
        this.pitch = pitch;
    }

    public void fadeOut() {
        this.shouldFadeOut = true;
    }

    @Override
    public void tick() {
        double fade;
        if (!shouldFadeOut) {
            this.currentTime++;
            fade = Math.min((double) this.currentTime / fadeTime, this.targetVolume);
        } else {
            this.currentTime--;
            fade = Math.max((double) this.currentTime / fadeTime, 0.0);
        }
        this.currentTime = Math.clamp(this.currentTime, 0, this.fadeTime);
        this.volume = (float) fade;

        if (this.volume <= 0.0) {
            this.setDone();
        }
    }
}
