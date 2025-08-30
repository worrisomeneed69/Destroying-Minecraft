package com.sp.block.entity.custom.voidblock;

import com.sp.block.custom.voidblock.VoidBlock;
import com.sp.block.entity.ModBlockEntities;
import com.sp.util.MathUtil;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class VoidBlockEntity extends BlockEntity {
    public float baseBrightness;
    public Object2BooleanArrayMap<Direction> cullMap = new Object2BooleanArrayMap<>();

    public VoidBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_BE, pos, state);
        this.baseBrightness = state.get(VoidBlock.GLITCHED) ? 1.0f : 0.1f;
    }



    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        //Simple culling. Really don't need anything else
        //TODO: Hard coded for the video remove before gameplay
        if (pos.getZ() >= -60 && world.isClient) {
            for (Direction direction : Direction.values()) {
                BlockState blockState = world.getBlockState(pos.add(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));

                cullMap.put(direction, blockState.getBlock() instanceof VoidBlock);
            }
        }
    }
}
