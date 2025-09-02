package com.sp.destruction;

import com.sp.cca.InitializeComponents;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.world.World;

public abstract class DestructionEvent {
    private final boolean isClient;
    private boolean shouldSkipKeyframe;
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
                this.animation.run();
                if (animation.wasKeyframeSkipped() || (animation.getProgress() % 200 == 0 && !world.isClient)) {
                    InitializeComponents.EVENTS.get(world).syncLight();
                }
            } else {
                animation.resetAnimation();
                this.resetEvent();
            }
        }
    }

    protected void skipKeyframe() {
        animation.skipKeyframe();
    }

    protected KeyframeAnimation initAnimations(World world) {
        return null;
    }

    public int getProgress() {
        return animation.progress;
    }

    public void setProgress(int progress) {
        if (this.isClient()) {
            animation.progress = progress;
        }
    }

    public void resetEvent() {
        this.active = false;
        if (this.animation != null) {
            this.animation.progress = 0;
        }
    }

    public boolean isClient() {
        return this.isClient;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active, long startTime) {
        if (active) {
            //Sync up with the server after packets arrive at the clients
            if (this.animation != null) {
                this.animation.progress = Math.toIntExact((System.currentTimeMillis() - startTime) / 50);
            }

        }
        this.active = active;
    }
}
