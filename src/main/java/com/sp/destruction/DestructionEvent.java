package com.sp.destruction;

import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.world.World;

public abstract class DestructionEvent {
    private final boolean isClient;
    protected boolean active;
    protected boolean initAnimations;
    protected KeyframeAnimation animation;
    protected int progress;
    protected final int duration;

    public DestructionEvent(int duration, boolean isClient) {
        this.duration = duration;
        this.isClient = isClient;
    }

    public void tick(World world) {
        if (!initAnimations) {
            animation = initAnimations(world);
            initAnimations = true;
        }

        if(animation != null) {
            if (this.isActive()) {
                this.progress++;
                animation.updateKeyframeAnimation((double) this.progress / this.duration);

            } else {
                animation.resetAnimation();
                this.resetEvent();
            }
        }
    }

    protected KeyframeAnimation initAnimations(World world) {
        return null;
    }

    protected void resetEvent() {
        this.active = false;
        this.progress = 0;
    }

    public boolean isClient() {
        return this.isClient;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
