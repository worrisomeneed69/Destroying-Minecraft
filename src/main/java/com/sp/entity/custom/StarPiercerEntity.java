package com.sp.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class StarPiercerEntity extends Entity {
    private static final TrackedData<Boolean> STARTUP = DataTracker.registerData(StarPiercerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> POWER_DOWN = DataTracker.registerData(StarPiercerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public StarPiercerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(STARTUP, false);
        builder.add(POWER_DOWN, false);
    }

    public boolean isStartingUp() {
        return this.dataTracker.get(STARTUP);
    }

    public boolean isPoweringDown() {
        return this.dataTracker.get(POWER_DOWN);
    }

    public void startup() {
        this.setData(true, false);
    }

    public void powerDown() {
        this.setData(false, true);
    }

    public void reset() {
        this.setData(false, false);
    }

    private void setData(boolean startup, boolean powerDown) {
        this.dataTracker.set(STARTUP, startup);
        this.dataTracker.set(POWER_DOWN, powerDown);
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
