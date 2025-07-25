package com.sp.block.entity.custom;

import com.sp.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class VoidBlockEntity extends BlockEntity {
    public VoidBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_BE, pos, state);
    }
}
