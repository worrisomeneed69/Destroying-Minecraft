package com.sp.entity.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;


public class SpinningBlockEntity extends Entity {
    private float acceleration;

    public SpinningBlockEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(this);
        if(this.getWorld().isClient) {
            this.acceleration += component.getAccelerationFactor();
            this.setPosition(this.getX(), this.getY() + this.acceleration, this.getZ());


            this.setPitch(this.getPitch() + component.getPitchIncrement());
            this.setYaw(this.getYaw() + component.getYawIncrement());
        } else {
            if(this.age > 59){
                this.discard();
            }
        }
    }


    @Override
    public boolean shouldRender(double distance) {
        return distance < 99999;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
