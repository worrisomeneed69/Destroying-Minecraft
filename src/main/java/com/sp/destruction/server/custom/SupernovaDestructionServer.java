package com.sp.destruction.server.custom;

import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SupernovaDestructionServer extends ServerDestructionEvent {

    public SupernovaDestructionServer() {
        super(3000);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(
                new Keyframe(0.0),

                new Keyframe(0.96, () -> {
                    DirectionalSBE explosion = new DirectionalSBE(50, 50, -90, 0.5f, new Vec3d(-1038, 77, 1325));
                    explosion.beginExplosion();
                })
        );
    }
}
