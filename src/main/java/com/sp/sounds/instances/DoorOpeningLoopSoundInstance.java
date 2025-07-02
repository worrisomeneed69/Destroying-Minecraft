package com.sp.sounds.instances;

import com.sp.sounds.ModSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class DoorOpeningLoopSoundInstance extends MovingSoundInstance {
    private boolean fadeOut;

    public DoorOpeningLoopSoundInstance(Vec3d pos) {
        super(ModSounds.DOOR_OPENING_LOOP, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.01f;
    }

    @Override
    public void tick() {
        if (this.fadeOut) {
            this.volume -= 0.05f;
        } else {
            this.volume = Math.min(this.volume + 0.02f, 1.0f);
        }

        if (this.volume <= 0.0f) {
            this.setDone();
        }
    }

    public void startFadeOut() {
        this.fadeOut = true;
    }
}
