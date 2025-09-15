package com.sp.destruction;

import com.sp.cca.InitializeComponents;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.world.World;

public abstract class DestructionEvent {
    private final boolean isClient;
    protected boolean active;
    protected boolean initAnimations;
    protected KeyframeAnimation animation;
    protected final int duration;

    public DestructionEvent(int duration, boolean isClient) {
        this.duration = duration;
        this.isClient = isClient;
    }

    public void tick(World world) {
        if (!initAnimations) {
            animation = initAnimations(world);
            animation.keyframeSkippedCallback(() -> {
                InitializeComponents.EVENTS.get(world).syncLight();
            });
            initAnimations = true;
        }

        if(animation != null) {
            if (this.isActive()) {
                this.animation.run(world);
                if ((animation.getProgress() % 200 == 0 && !world.isClient)) {
                    InitializeComponents.EVENTS.get(world).syncLight();
                }
            } else {
                this.resetEvent();
            }
        }
    }

    public void resetAnimationToCurrentTime(World world) {
        this.animation.resetToCurrentTime(world);
    }

    protected void skipKeyframe() {
        animation.skipKeyframe();
    }

    protected KeyframeAnimation initAnimations(World world) {
        return null;
    }

    public int getProgress() {
        return animation.skippedTime;
    }

    public void setProgress(int progress) {
        if (this.isClient() && this.animation != null) {
            animation.skippedTime = progress;
        }
    }

    public void resetAnimations() {
        this.initAnimations = false;
        this.animation = null;
    }

    public void resetEvent() {
        this.active = false;
        if (this.animation != null) {
            this.animation.resetAnimation();
        }
    }

    public boolean isClient() {
        return this.isClient;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active, long startTime) {
//        if (active) {
//            //Sync up with the server after packets arrive at the clients
//            if (this.animation != null) {
//                this.animation.startTime = Math.toIntExact((System.currentTimeMillis() - startTime) / 50);
//            }
//        }
        this.active = active;
    }
}
