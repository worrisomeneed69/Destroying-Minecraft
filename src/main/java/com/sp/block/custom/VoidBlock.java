package com.sp.block.custom;

import com.mojang.serialization.MapCodec;
import com.sp.block.entity.custom.VoidBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VoidBlock extends BlockWithEntity {
    public static final MapCodec<VoidBlock> CODEC = createCodec(VoidBlock::new);

    public VoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VoidBlockEntity(pos, state);
    }
}
