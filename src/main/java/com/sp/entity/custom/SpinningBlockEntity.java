package com.sp.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.world.World;

import java.util.List;

public class SpinningBlockEntity extends DisplayEntity.BlockDisplayEntity {
    private boolean init;
    private float acceleration;
    private final float pitchIncrement;
    private final float yawIncrement;

    private static final List<BlockState> randomBlocks = List.of(
            Blocks.DIRT.getDefaultState(),
            Blocks.STONE.getDefaultState(),
            Blocks.GRAVEL.getDefaultState(),
            Blocks.DEEPSLATE.getDefaultState(),
            Blocks.GRASS_BLOCK.getDefaultState()
    );

    public SpinningBlockEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.pitchIncrement = this.getRandom().nextFloat()*5;
        this.yawIncrement = this.getRandom().nextFloat()*5;
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.init){
            this.setBlockState(
                    randomBlocks.get( this.random.nextBetween(0, randomBlocks.size() - 1) )
            );
            this.init = true;
        }

//        this.acceleration += 0.01f;
//        this.setPosition(this.getX(), this.getY() - this.acceleration, this.getZ());

        if(this.getWorld().isClient) {
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
            this.setPitch(this.getPitch(tickDelta) + this.pitchIncrement);
            this.setYaw(this.getYaw(tickDelta) + this.yawIncrement);
        } else {
            if(this.getWorld().getBlockState(this.getBlockPos()).isSolid()){
                this.discard();
            }
        }
    }
}
