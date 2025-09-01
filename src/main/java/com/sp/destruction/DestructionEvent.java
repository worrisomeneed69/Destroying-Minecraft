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
                this.progress++;
                if (shouldSkipKeyframe) {
                    double nextKeyframeTime = animation.getNextKeyframeTime();
                    this.progress = (int) Math.floor(this.duration * nextKeyframeTime);
                    InitializeComponents.EVENTS.get(world).syncLight();

                    this.shouldSkipKeyframe = false;
                }

                animation.updateKeyframeAnimation((double) this.progress / this.duration);

                if (progress % 200 == 0 && !this.isClient) {
                    InitializeComponents.EVENTS.get(world).syncLight();
                }
            } else {
                animation.resetAnimation();
                this.resetEvent();
            }
        }
    }

    protected void skipKeyframe() {
        this.shouldSkipKeyframe = true;
    }

    protected void sync() {

    }

    protected KeyframeAnimation initAnimations(World world) {
        return null;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (this.isClient()) {
            this.progress = progress;
        }
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

    public void setActive(boolean active, long startTime) {
        if (active) {
            //Sync up with the server after packets arrive at the clients
            this.progress = Math.toIntExact((System.currentTimeMillis() - startTime) / 50);
        }
        this.active = active;
    }
}
