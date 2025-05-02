package com.sp.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class SpinningBlockEntity extends Entity {
    private boolean init;
    private float acceleration;
    private final float pitchIncrement;
    private final float yawIncrement;

    private static final TrackedData<Float> ACCELERATION_FACTOR = DataTracker.registerData(
            SpinningBlockEntity.class, TrackedDataHandlerRegistry.FLOAT
    );

    private static final List<BlockState> randomBlocks = List.of(
            Blocks.DIRT.getDefaultState(),
            Blocks.STONE.getDefaultState(),
            Blocks.GRAVEL.getDefaultState(),
            Blocks.DEEPSLATE.getDefaultState(),
            Blocks.GRASS_BLOCK.getDefaultState()
    );

    public SpinningBlockEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.pitchIncrement = this.getRandom().nextFloat()*20;
        this.yawIncrement = this.getRandom().nextFloat()*20;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ACCELERATION_FACTOR, 0.1f);
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.init && !this.getWorld().isClient) {
            this.setAccelerationFactor(this.getRandom().nextFloat()*0.1f);
            this.init = true;
        }


        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        if(this.getWorld().isClient) {
            this.acceleration += this.getAccelerationFactor();
            this.setPosition(this.getX(), this.getY() + this.acceleration, this.getZ());


            this.setPitch(this.getPitch(tickDelta) + this.pitchIncrement);
            this.setYaw(this.getYaw(tickDelta) + this.yawIncrement);
        } else {
            this.acceleration += this.getAccelerationFactor();
            this.setPosition(this.getX(), this.getY() + this.acceleration, this.getZ());

            if(this.age > 100){
                this.discard();
            }
        }
    }


    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setAccelerationFactor(nbt.getFloat("acc_fac"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("acc_fac", this.getAccelerationFactor());
    }

    public final Float getAccelerationFactor() {
        return this.dataTracker.get(ACCELERATION_FACTOR);
    }
    public final void setAccelerationFactor(Float factor) {
        this.dataTracker.set(ACCELERATION_FACTOR, factor);
    }
}
