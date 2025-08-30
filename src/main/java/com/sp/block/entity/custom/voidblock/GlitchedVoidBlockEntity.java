package com.sp.block.entity.custom.voidblock;

import com.sp.block.custom.voidblock.GlitchedVoidBlock;
import com.sp.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GlitchedVoidBlockEntity extends BlockEntity {
    public GlitchedVoidBlockEntity originalEntity;
    public float startTime;
    public float fadeTime;
    public float currentBrightness;
    public float targetBrightness;
    public float rng;
    public final float baseBrightness;

    public GlitchedVoidBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLITCHED_VOID_BE, pos, state);
        this.fadeTime = 0.0001f * state.get(GlitchedVoidBlock.FADE_TIME);
        this.baseBrightness = 0.1f;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient && originalEntity == null) {
            BlockEntity blockEntity2 = world.getBlockEntity(pos.east());
            if (blockEntity2 instanceof GlitchedVoidBlockEntity glitchedVoidBlockEntity) {
                originalEntity = glitchedVoidBlockEntity.originalEntity;
                return;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos.down());
            if (blockEntity instanceof GlitchedVoidBlockEntity glitchedVoidBlockEntity) {
                originalEntity = glitchedVoidBlockEntity.originalEntity;
                return;
            }

            BlockEntity blockEntity3 = world.getBlockEntity(pos.south());
            if (blockEntity3 instanceof GlitchedVoidBlockEntity glitchedVoidBlockEntity) {
                originalEntity = glitchedVoidBlockEntity.originalEntity;
                return;
            }

            originalEntity = this;
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
    }


}
