package com.sp.destruction.server.custom.blackhole;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.entity.custom.BlockPhysicsEntity;
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
    private static final List<BlockPos> initialBPEPositions = List.of(
            new BlockPos(-1151, 77, 398),
            new BlockPos(-1152, 78, 398),
            new BlockPos(-1151, 78, 398),
            new BlockPos(-1149, 78, 398),
            new BlockPos(-1153, 79, 398),
            new BlockPos(-1152, 79, 398),
            new BlockPos(-1151, 79, 398),
            new BlockPos(-1150, 79, 398),
            new BlockPos(-1149, 79, 398),
            new BlockPos(-1152, 80, 398),
            new BlockPos(-1151, 80, 398),
            new BlockPos(-1150, 80, 398),
            new BlockPos(-1153, 76, 399),
            new BlockPos(-1152, 76, 399),
            new BlockPos(-1151, 76, 399),
            new BlockPos(-1150, 76, 399),
            new BlockPos(-1154, 77, 399),
            new BlockPos(-1153, 77, 399),
            new BlockPos(-1152, 77, 399),
            new BlockPos(-1151, 77, 399),
            new BlockPos(-1150, 77, 399),
            new BlockPos(-1149, 77, 399),
            new BlockPos(-1148, 77, 399),
            new BlockPos(-1153, 78, 399),
            new BlockPos(-1152, 78, 399),
            new BlockPos(-1151, 78, 399),
            new BlockPos(-1150, 78, 399),
            new BlockPos(-1149, 78, 399),
            new BlockPos(-1148, 78, 399),
            new BlockPos(-1153, 79, 399),
            new BlockPos(-1152, 79, 399),
            new BlockPos(-1151, 79, 399),
            new BlockPos(-1150, 79, 399),
            new BlockPos(-1149, 79, 399),
            new BlockPos(-1148, 79, 399),
            new BlockPos(-1152, 80, 399),
            new BlockPos(-1151, 80, 399),
            new BlockPos(-1150, 80, 399),
            new BlockPos(-1153, 77, 400),
            new BlockPos(-1152, 77, 400),
            new BlockPos(-1151, 77, 400),
            new BlockPos(-1150, 77, 400),
            new BlockPos(-1149, 77, 400),
            new BlockPos(-1154, 78, 400),
            new BlockPos(-1153, 78, 400),
            new BlockPos(-1152, 78, 400),
            new BlockPos(-1151, 78, 400),
            new BlockPos(-1150, 78, 400),
            new BlockPos(-1149, 78, 400),
            new BlockPos(-1148, 78, 400),
            new BlockPos(-1154, 79, 400),
            new BlockPos(-1153, 79, 400),
            new BlockPos(-1152, 79, 400),
            new BlockPos(-1151, 79, 400),
            new BlockPos(-1150, 79, 400),
            new BlockPos(-1149, 79, 400),
            new BlockPos(-1148, 79, 400),
            new BlockPos(-1153, 80, 400),
            new BlockPos(-1152, 80, 400),
            new BlockPos(-1151, 80, 400),
            new BlockPos(-1150, 80, 400),
            new BlockPos(-1149, 80, 400),
            new BlockPos(-1152, 81, 400),
            new BlockPos(-1151, 81, 400),
            new BlockPos(-1150, 81, 400),
            new BlockPos(-1153, 76, 401),
            new BlockPos(-1152, 76, 401),
            new BlockPos(-1151, 76, 401),
            new BlockPos(-1154, 77, 401),
            new BlockPos(-1153, 77, 401),
            new BlockPos(-1152, 77, 401),
            new BlockPos(-1151, 77, 401),
            new BlockPos(-1150, 77, 401),
            new BlockPos(-1149, 77, 401),
            new BlockPos(-1148, 77, 401),
            new BlockPos(-1154, 78, 401),
            new BlockPos(-1153, 78, 401),
            new BlockPos(-1152, 78, 401),
            new BlockPos(-1151, 78, 401),
            new BlockPos(-1150, 78, 401),
            new BlockPos(-1149, 78, 401),
            new BlockPos(-1148, 78, 401),
            new BlockPos(-1155, 79, 401),
            new BlockPos(-1154, 79, 401),
            new BlockPos(-1153, 79, 401),
            new BlockPos(-1152, 79, 401),
            new BlockPos(-1151, 79, 401),
            new BlockPos(-1150, 79, 401),
            new BlockPos(-1149, 79, 401),
            new BlockPos(-1148, 79, 401),
            new BlockPos(-1154, 80, 401),
            new BlockPos(-1153, 80, 401),
            new BlockPos(-1152, 80, 401),
            new BlockPos(-1151, 80, 401),
            new BlockPos(-1150, 80, 401),
            new BlockPos(-1149, 80, 401),
            new BlockPos(-1148, 80, 401),
            new BlockPos(-1153, 81, 401),
            new BlockPos(-1152, 81, 401),
            new BlockPos(-1151, 81, 401),
            new BlockPos(-1150, 81, 401),
            new BlockPos(-1149, 81, 401),
            new BlockPos(-1154, 76, 402),
            new BlockPos(-1153, 76, 402),
            new BlockPos(-1152, 76, 402),
            new BlockPos(-1151, 76, 402),
            new BlockPos(-1150, 76, 402),
            new BlockPos(-1155, 77, 402),
            new BlockPos(-1154, 77, 402),
            new BlockPos(-1153, 77, 402),
            new BlockPos(-1152, 77, 402),
            new BlockPos(-1151, 77, 402),
            new BlockPos(-1150, 77, 402),
            new BlockPos(-1149, 77, 402),
            new BlockPos(-1148, 77, 402),
            new BlockPos(-1155, 78, 402),
            new BlockPos(-1154, 78, 402),
            new BlockPos(-1153, 78, 402),
            new BlockPos(-1152, 78, 402),
            new BlockPos(-1151, 78, 402),
            new BlockPos(-1150, 78, 402),
            new BlockPos(-1149, 78, 402),
            new BlockPos(-1148, 78, 402),
            new BlockPos(-1155, 79, 402),
            new BlockPos(-1154, 79, 402),
            new BlockPos(-1153, 79, 402),
            new BlockPos(-1152, 79, 402),
            new BlockPos(-1151, 79, 402),
            new BlockPos(-1150, 79, 402),
            new BlockPos(-1149, 79, 402),
            new BlockPos(-1148, 79, 402),
            new BlockPos(-1147, 79, 402),
            new BlockPos(-1155, 80, 402),
            new BlockPos(-1154, 80, 402),
            new BlockPos(-1153, 80, 402),
            new BlockPos(-1152, 80, 402),
            new BlockPos(-1151, 80, 402),
            new BlockPos(-1150, 80, 402),
            new BlockPos(-1149, 80, 402),
            new BlockPos(-1148, 80, 402),
            new BlockPos(-1147, 80, 402),
            new BlockPos(-1154, 81, 402),
            new BlockPos(-1153, 81, 402),
            new BlockPos(-1152, 81, 402),
            new BlockPos(-1151, 81, 402),
            new BlockPos(-1150, 81, 402),
            new BlockPos(-1149, 81, 402),
            new BlockPos(-1148, 81, 402),
            new BlockPos(-1154, 76, 403),
            new BlockPos(-1153, 76, 403),
            new BlockPos(-1152, 76, 403),
            new BlockPos(-1151, 76, 403),
            new BlockPos(-1150, 76, 403),
            new BlockPos(-1155, 77, 403),
            new BlockPos(-1154, 77, 403),
            new BlockPos(-1153, 77, 403),
            new BlockPos(-1152, 77, 403),
            new BlockPos(-1151, 77, 403),
            new BlockPos(-1150, 77, 403),
            new BlockPos(-1149, 77, 403),
            new BlockPos(-1148, 77, 403),
            new BlockPos(-1155, 78, 403),
            new BlockPos(-1154, 78, 403),
            new BlockPos(-1153, 78, 403),
            new BlockPos(-1152, 78, 403),
            new BlockPos(-1151, 78, 403),
            new BlockPos(-1150, 78, 403),
            new BlockPos(-1149, 78, 403),
            new BlockPos(-1148, 78, 403),
            new BlockPos(-1155, 79, 403),
            new BlockPos(-1154, 79, 403),
            new BlockPos(-1153, 79, 403),
            new BlockPos(-1152, 79, 403),
            new BlockPos(-1151, 79, 403),
            new BlockPos(-1150, 79, 403),
            new BlockPos(-1149, 79, 403),
            new BlockPos(-1148, 79, 403),
            new BlockPos(-1155, 80, 403),
            new BlockPos(-1154, 80, 403),
            new BlockPos(-1153, 80, 403),
            new BlockPos(-1152, 80, 403),
            new BlockPos(-1151, 80, 403),
            new BlockPos(-1150, 80, 403),
            new BlockPos(-1149, 80, 403),
            new BlockPos(-1148, 80, 403),
            new BlockPos(-1155, 81, 403),
            new BlockPos(-1154, 81, 403),
            new BlockPos(-1153, 81, 403),
            new BlockPos(-1152, 81, 403),
            new BlockPos(-1151, 81, 403),
            new BlockPos(-1150, 81, 403),
            new BlockPos(-1149, 81, 403),
            new BlockPos(-1148, 81, 403),
            new BlockPos(-1147, 81, 403),
            new BlockPos(-1153, 76, 404),
            new BlockPos(-1154, 77, 404),
            new BlockPos(-1153, 77, 404),
            new BlockPos(-1152, 77, 404),
            new BlockPos(-1151, 77, 404),
            new BlockPos(-1150, 77, 404),
            new BlockPos(-1149, 77, 404),
            new BlockPos(-1148, 77, 404),
            new BlockPos(-1154, 78, 404),
            new BlockPos(-1153, 78, 404),
            new BlockPos(-1152, 78, 404),
            new BlockPos(-1151, 78, 404),
            new BlockPos(-1150, 78, 404),
            new BlockPos(-1149, 78, 404),
            new BlockPos(-1148, 78, 404),
            new BlockPos(-1155, 79, 404),
            new BlockPos(-1154, 79, 404),
            new BlockPos(-1153, 79, 404),
            new BlockPos(-1152, 79, 404),
            new BlockPos(-1151, 79, 404),
            new BlockPos(-1150, 79, 404),
            new BlockPos(-1149, 79, 404),
            new BlockPos(-1148, 79, 404),
            new BlockPos(-1155, 80, 404),
            new BlockPos(-1154, 80, 404),
            new BlockPos(-1153, 80, 404),
            new BlockPos(-1152, 80, 404),
            new BlockPos(-1151, 80, 404),
            new BlockPos(-1150, 80, 404),
            new BlockPos(-1149, 80, 404),
            new BlockPos(-1148, 80, 404),
            new BlockPos(-1155, 81, 404),
            new BlockPos(-1154, 81, 404),
            new BlockPos(-1153, 81, 404),
            new BlockPos(-1152, 81, 404),
            new BlockPos(-1151, 81, 404),
            new BlockPos(-1150, 81, 404),
            new BlockPos(-1149, 81, 404),
            new BlockPos(-1148, 81, 404),
            new BlockPos(-1147, 81, 404),
            new BlockPos(-1154, 77, 405),
            new BlockPos(-1153, 77, 405),
            new BlockPos(-1152, 77, 405),
            new BlockPos(-1151, 77, 405),
            new BlockPos(-1150, 77, 405),
            new BlockPos(-1154, 78, 405),
            new BlockPos(-1153, 78, 405),
            new BlockPos(-1152, 78, 405),
            new BlockPos(-1151, 78, 405),
            new BlockPos(-1150, 78, 405),
            new BlockPos(-1154, 79, 405),
            new BlockPos(-1153, 79, 405),
            new BlockPos(-1152, 79, 405),
            new BlockPos(-1151, 79, 405),
            new BlockPos(-1150, 79, 405),
            new BlockPos(-1149, 79, 405),
            new BlockPos(-1148, 79, 405),
            new BlockPos(-1154, 80, 405),
            new BlockPos(-1153, 80, 405),
            new BlockPos(-1152, 80, 405),
            new BlockPos(-1151, 80, 405),
            new BlockPos(-1150, 80, 405),
            new BlockPos(-1149, 80, 405),
            new BlockPos(-1148, 80, 405),
            new BlockPos(-1155, 81, 405),
            new BlockPos(-1154, 81, 405),
            new BlockPos(-1153, 81, 405),
            new BlockPos(-1152, 81, 405),
            new BlockPos(-1151, 81, 405),
            new BlockPos(-1150, 81, 405),
            new BlockPos(-1149, 81, 405),
            new BlockPos(-1148, 81, 405),
            new BlockPos(-1152, 77, 406),
            new BlockPos(-1151, 77, 406),
            new BlockPos(-1152, 78, 406),
            new BlockPos(-1151, 78, 406),
            new BlockPos(-1150, 78, 406),
            new BlockPos(-1153, 79, 406),
            new BlockPos(-1152, 79, 406),
            new BlockPos(-1151, 79, 406),
            new BlockPos(-1150, 79, 406),
            new BlockPos(-1149, 79, 406),
            new BlockPos(-1153, 80, 406),
            new BlockPos(-1152, 80, 406),
            new BlockPos(-1151, 80, 406),
            new BlockPos(-1150, 80, 406),
            new BlockPos(-1154, 81, 406),
            new BlockPos(-1153, 81, 406),
            new BlockPos(-1152, 81, 406),
            new BlockPos(-1151, 81, 406),
            new BlockPos(-1150, 81, 406),
            new BlockPos(-1149, 81, 406),
            new BlockPos(-1148, 81, 406),
            new BlockPos(-1150, 79, 407),
            new BlockPos(-1151, 80, 407),
            new BlockPos(-1150, 80, 407),
            new BlockPos(-1151, 81, 407),
            new BlockPos(-1150, 81, 407)
    );
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
        super(3400);
    }

    @Override
    public void resetEvent() {
        if (entity != null) {
            System.out.println("NOT WORKING");
            entity.discard();
            entity = null;
        }
        super.resetEvent();
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(world);

        return new KeyframeAnimation(
                this.duration,
                (globalTime, localTime) -> {
                    double clampedGlobalTime = Math.floor(globalTime * 10) * 0.1;
                    if (clampedGlobalTime != prevGravityLerp) {
                        component.setGravityLerp(clampedGlobalTime);
                        component.syncLight();
                        prevGravityLerp = clampedGlobalTime;

                        BRAAMS.forEach((aDouble, soundEvent) -> {
                            if (MathHelper.approximatelyEquals(clampedGlobalTime, aDouble)) {
                                for (PlayerEntity player : world.getPlayers()) {
                                    DestroyingMinecraft.sendBraamPacket(player, soundEvent);
                                }
                            }
                        });
                    }
                },

                //End action
                () -> {
                    prevGravityLerp = 0.0;
                    component.setGravityLerp(1.2);
                    component.syncLight();

                    for (PlayerEntity player : world.getPlayers()) {
                        DestroyingMinecraft.sendBraamPacket(player, ModSounds.BLACK_HOLE_BRAAM_FINAL);
                    }
                },

                new Keyframe(0.0),

                new Keyframe(135.0 / this.duration, () -> {
                    entity = BlockPhysicsEntity.ofBlocks(world, initialBPEPositions);
                    entity.setVelocity(0, 0.06, -0.2);
                    entity.component.setRotationSpeed(0.5f, 0, 0);
                    entity.component.sync();
                    entity.velocityDirty = true;
                    entity.velocityModified = true;
                }),

                new Keyframe(155.0 / this.duration, () -> {
                    BlackHoleDestruction.setStartDestruction(true);
                })
        );
    }

}
