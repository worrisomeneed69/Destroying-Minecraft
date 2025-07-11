package com.sp.block.custom;

import com.mojang.serialization.MapCodec;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.block.*;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChairBlock extends HorizontalFacingBlock {
    public static final MapCodec<ChairBlock> CODEC = createCodec(ChairBlock::new);
    public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;

    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            BOTTOM_SHAPE,
            Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            BOTTOM_SHAPE,
            Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 16.0)
    );
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            BOTTOM_SHAPE,
            Block.createCuboidShape(0.0, 8.0, 8.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            BOTTOM_SHAPE,
            Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 8.0)
    );

    public ChairBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case NORTH -> NORTH_SHAPE;
            default -> SOUTH_SHAPE;
        };
    }

    public static Direction getDirection(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock() instanceof ChairBlock ? blockState.get(FACING) : null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.CONSUME;
        } else {
            if (state.get(OCCUPIED)) {
                player.sendMessage(Text.literal("This chair is occupied"), true);
            } else {
                player.setPose(EntityPose.SLEEPING);
                if (player instanceof LayingDownPlayerEntity layingDownPLayerEntity) {
                    layingDownPLayerEntity.setLayingDown(true);
                    layingDownPLayerEntity.setLayingDownPos(pos);
                }
                player.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                player.setVelocity(Vec3d.ZERO);

                world.setBlockState(pos, state.with(OCCUPIED, true));
            }
            return ActionResult.SUCCESS;
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OCCUPIED, false)
                .with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(OCCUPIED, FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}
