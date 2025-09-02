package com.sp.destruction.server.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class LaserDestructionServer extends ServerDestructionEvent {
    public static float laserLength;
    public static float crackingTime;

    public LaserDestructionServer() {
        super(2500);
    }

    @Override
    public void resetEvent() {
        crackingTime = 0.0f;
        laserLength = 0.0f;
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(
                this.duration,
                //End Action
                () -> {
                    for (PlayerEntity player : world.getPlayers()) {
                        DestroyingMinecraft.sendWaitingRoomPacket(player, true);
                    }
                },
                new Keyframe(0.0),

                new Keyframe(484.0 / this.duration, () -> {
                    laserLength = 1.0f;
                }),

                new Keyframe(700.0 / this.duration, (globalTime, localTime) -> {
                    crackingTime = (float) localTime;
                    int playerCount = 0;
                    for (PlayerEntity player : world.getPlayers()) {
                        if (player.isCreative() || player.isSpectator()) continue;
                        playerCount++;
                    }
                    System.out.println(playerCount);
                    if (playerCount <= 0) {
                        System.out.println("SKIPPING");
                        this.skipKeyframe();
                    }
                }),

                new Keyframe(2100.0 / this.duration, () -> {

                })
        );
    }
}
