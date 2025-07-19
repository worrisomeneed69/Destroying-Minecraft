package com.sp.destruction.server.custom.blackhole;

import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.world.World;

public class BlackHoleDestructionServerPart1 extends ServerDestructionEvent {

    public BlackHoleDestructionServerPart1() {
        super(540);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(new Keyframe(0.0f));
    }

}
