package com.sp.destruction.server.custom.blackhole;

import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlackHoleDestructionServerPart1 extends ServerDestructionEvent {

    public BlackHoleDestructionServerPart1() {
        super(540);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(
                this.duration,
                new Keyframe(0.0, () -> {
                    world.setBlockState(new BlockPos(-1156, 84, 425), Blocks.REDSTONE_BLOCK.getDefaultState());
                }),

                new Keyframe(0.9f, () -> {
                    world.setBlockState(new BlockPos(-1156, 84, 425), Blocks.REDSTONE_BLOCK.getDefaultState());
                })
        );
    }

}
