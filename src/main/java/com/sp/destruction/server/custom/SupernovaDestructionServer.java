package com.sp.destruction.server.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.networking.ServerPacketManager;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SupernovaDestructionServer extends ServerDestructionEvent {

    public SupernovaDestructionServer() {
        super(2900);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,

                new Keyframe(0.0),

                new Keyframe(2880.0/this.duration, () -> {
                    DirectionalSBE explosion = new DirectionalSBE(50, 50, -90, 0.5f, new Vec3d(-1038, 77, 1325));
                    explosion.beginExplosion((ServerWorld) world);
                })
        ).endAction(() -> {
            for (PlayerEntity player : world.getPlayers()) {
                ServerPacketManager.sendWaitingRoomPacket(player, true);
            }
            this.setActive(false, -1);
            this.resetEvent();
        }).build();
    }
}
