package com.sp.destruction.server.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.DestructionType;
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
        super(DestructionType.SUPERNOVA, 2930);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(world);

        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,

                new Keyframe(0.0),

                new Keyframe(2880.0/this.duration, () -> {
                    DirectionalSBE explosion = new DirectionalSBE(50, 80, -90, 0.2f, component.getDestructionEventPosition());
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
