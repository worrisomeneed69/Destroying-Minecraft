package com.sp.destruction.server.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Set;

public class InitializeDestructionServer extends ServerDestructionEvent {

    public InitializeDestructionServer() {
        super(250);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,

                new Keyframe(0.0),

                new Keyframe(200.0 / this.duration, () -> {
                    for (PlayerEntity player : world.getPlayers()) {
                        if (player.isSpectator()) continue;

                        Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
                        player.teleport((ServerWorld) world, -1199.5, 137.4, 1226.5, set, 0, 0);
                    }
                }),

                new Keyframe(230.0 / this.duration, () -> {
                    PointSBE pointSBE = new PointSBE(4, 0.5f, new Vec3d(-1204.5, 66.5, 1209.5));
                    pointSBE.beginExplosion((ServerWorld) world);
                }),

                new Keyframe(240.0 / this.duration, () -> {
                    PointSBE pointSBE = new PointSBE(4, 0.5f, new Vec3d(-1191.5, 67.5, 1218.5));
                    pointSBE.beginExplosion((ServerWorld) world);
                }),

                new Keyframe(249.0 / this.duration, () -> {
                    PointSBE pointSBE = new PointSBE(4, 0.5f, new Vec3d(-1199.5, 67.5, 1226.5));
                    pointSBE.beginExplosion((ServerWorld) world);
                })
        ).build();
    }
}
