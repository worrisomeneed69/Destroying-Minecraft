package com.sp.destruction.server.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.DestructionType;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.networking.ServerPacketManager;
import com.sp.sounds.ModSounds;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BlackHoleDestructionServerPart2 extends ServerDestructionEvent {
    private static BlockPhysicsEntity entity;
    private static double prevGravityLerp;
    private static final Double2ObjectMap<SoundEvent> BRAAMS = new Double2ObjectArrayMap<>() {
        {
            put(0.2, ModSounds.BLACK_HOLE_BRAAM1);
            put(0.4, ModSounds.BLACK_HOLE_BRAAM2);
            put(0.6, ModSounds.BLACK_HOLE_BRAAM3);
            put(0.8, ModSounds.BLACK_HOLE_BRAAM1);
        }
    };

    public BlackHoleDestructionServerPart2() {
        super(DestructionType.BLACK_HOLE, 3400);
    }

    @Override
    public void resetEvent() {
        if (entity != null) {
            entity.discard();
            entity = null;
        }
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(world);

        return new KeyframeAnimation.KeyframeAnimationBuilder(
            this.duration,

            new Keyframe(0.0),

            new Keyframe(155.0 / this.duration, () -> {
                BlackHoleDestruction.setStartDestruction(true);
            })
        ).globalAction((globalTime, localTime) -> {
            double clampedGlobalTime = Math.floor(globalTime * 10) * 0.1;
            if (clampedGlobalTime != prevGravityLerp) {
                component.setGravityLerp(clampedGlobalTime);
                component.syncLight();
                prevGravityLerp = clampedGlobalTime;

                BRAAMS.forEach((aDouble, soundEvent) -> {
                    if (MathHelper.approximatelyEquals(clampedGlobalTime, aDouble)) {
                        for (PlayerEntity player : world.getPlayers()) {
                            ServerPacketManager.sendBraamPacket(player, soundEvent);
                        }
                    }
                });
            }
        }).endAction(() -> {
            prevGravityLerp = 0.0;
            component.setGravityLerp(1.2);
            component.syncLight();

            for (PlayerEntity player : world.getPlayers()) {
                ServerPacketManager.sendBraamPacket(player, ModSounds.BLACK_HOLE_BRAAM_FINAL);
            }
        }).build();
    }

}
