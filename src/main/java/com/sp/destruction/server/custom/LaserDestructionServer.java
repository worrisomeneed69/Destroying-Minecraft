package com.sp.destruction.server.custom;

import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.world.World;

public class LaserDestructionServer extends ServerDestructionEvent {
    public static float laserLength;
    public static float crackingTime;

    public LaserDestructionServer() {
        super(2400);
    }

    @Override
    protected void resetEvent() {
        crackingTime = 0.0f;
        laserLength = 0.0f;
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(
                new Keyframe(0.0),

                new Keyframe(484.0 / this.duration, () -> {
                    laserLength = 1.0f;
                }),

                new Keyframe(700.0 / this.duration, (globalTime, localTime) -> {
                    crackingTime = (float) localTime;
                })
        );
    }
}
