package com.sp.destruction.server.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.networking.ServerPacketManager;
import com.sp.sounds.ModSounds;
import com.sp.util.MathUtil;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LaserDestructionServer extends ServerDestructionEvent {
    private static final Random random = Random.create();
    private static final Vec3d centerPos = new Vec3d(-1716.5, 66, 1563.5);
    private static final float radius = 23;
    private static int lavaSpewDelay = 200;

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
        return new KeyframeAnimation.KeyframeAnimationBuilder(
            this.duration,
            new Keyframe(0.0),

            new Keyframe(484.0 / this.duration, () -> {
                laserLength = 1.0f;
            }),

            new Keyframe(700.0 / this.duration, (globalTime, localTime) -> {
                crackingTime = (float) localTime;

                if (lavaSpewDelay <= 0) {
                    float maxRadius = crackingTime * radius;
                    Vec3d offset = new Vec3d(MathUtil.nextBetween(-maxRadius, maxRadius), 0, MathUtil.nextBetween(-maxRadius, maxRadius));
                    offset = offset.add(centerPos);

                    for (PlayerEntity player : world.getPlayers()) {
                        ServerPacketManager.sendLavaSpewPacket(player, offset);
                    }
                    world.playSound(null, offset.x, offset.y, offset.z, ModSounds.LAVA_SPEW, SoundCategory.AMBIENT, 10.0f, MathUtil.nextBetween(0.8f, 1.2f));

                    lavaSpewDelay = random.nextBetween(30, 100);
                } else {
                    lavaSpewDelay--;
                }

                int playerCount = 0;

                for (PlayerEntity player : world.getPlayers()) {
                    if (player.isCreative() || player.isSpectator()) continue;
                    playerCount++;
                }

                if (playerCount <= 1) {
//                    this.skipKeyframe();
                }
            }),

            new Keyframe(2100.0 / this.duration)
        ).endAction(() -> {
            for (PlayerEntity player : world.getPlayers()) {
                ServerPacketManager.sendWaitingRoomPacket(player, true);
            }
        }).build();
    }
}
