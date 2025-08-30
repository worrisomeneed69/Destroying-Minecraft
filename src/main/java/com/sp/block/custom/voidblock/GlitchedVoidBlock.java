package com.sp.block.custom.voidblock;

import com.mojang.serialization.MapCodec;
import com.sp.block.entity.ModBlockEntities;
import com.sp.block.entity.custom.voidblock.GlitchedVoidBlockEntity;
import com.sp.block.entity.custom.voidblock.VoidBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlitchedVoidBlock extends BlockWithEntity {
    public static final IntProperty FADE_TIME = IntProperty.of("fade_time", 1, 10);
    public static final MapCodec<GlitchedVoidBlock> CODEC = createCodec(GlitchedVoidBlock::new);

    public GlitchedVoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof GlitchedVoidBlockEntity glitchedVoidBlockEntity) {
            int fadeTime = state.get(FADE_TIME);
            world.setBlockState(pos, state.with(FADE_TIME, fadeTime >= 10 ? 1 : fadeTime + 1));
            glitchedVoidBlockEntity.fadeTime = 0.0001f * state.get(GlitchedVoidBlock.FADE_TIME);
        }

        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.GLITCHED_VOID_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GlitchedVoidBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FADE_TIME);
    }
}
