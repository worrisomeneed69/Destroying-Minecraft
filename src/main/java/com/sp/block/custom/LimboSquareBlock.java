package com.sp.block.custom;

import com.mojang.serialization.MapCodec;
import com.sp.block.entity.custom.LimboSquareBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LimboSquareBlock extends BlockWithEntity {
    public static final MapCodec<LimboSquareBlock> CODEC = createCodec(LimboSquareBlock::new);

    public LimboSquareBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LimboSquareBlockEntity limboSquareBlockEntity) {
            return limboSquareBlockEntity.openScreen(player) ? ActionResult.success(world.isClient) : ActionResult.PASS;
        }


        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LimboSquareBlockEntity(pos, state);
    }
}
