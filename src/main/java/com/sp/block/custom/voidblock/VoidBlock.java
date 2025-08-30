package com.sp.block.custom.voidblock;

import com.mojang.serialization.MapCodec;
import com.sp.block.entity.custom.voidblock.VoidBlockEntity;
import com.sp.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VoidBlock extends BlockWithEntity {
    public static final BooleanProperty GLITCHED = BooleanProperty.of("glitched");
    public static final MapCodec<VoidBlock> CODEC = createCodec(VoidBlock::new);

    public VoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof VoidBlockEntity voidBlockEntity) {
            boolean isGlitched = state.get(GLITCHED);
            world.setBlockState(pos, state.with(GLITCHED, !isGlitched));
            voidBlockEntity.baseBrightness = isGlitched ? 0.1f : 1.0f;
        }

        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VoidBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GLITCHED);
    }
}
