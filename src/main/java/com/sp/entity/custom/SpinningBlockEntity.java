package com.sp.entity.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;


public class SpinningBlockEntity extends Entity {
    private boolean init;
    private float acceleration;

    private static final TrackedData<Float> ACCELERATION_FACTOR = DataTracker.registerData(
            SpinningBlockEntity.class, TrackedDataHandlerRegistry.FLOAT
    );

    public SpinningBlockEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ACCELERATION_FACTOR, 0.1f);
    }

    @Override
    public void tick() {
        if(!this.init && !this.getWorld().isClient) {
            this.setAccelerationFactor(this.getRandom().nextFloat()*0.05f + 0.3f);
            this.init = true;
        }


        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(this);
        if(this.getWorld().isClient) {

            this.acceleration += this.getAccelerationFactor();
//            Vec3d pos = this.getLerpedPos(tickDelta);
            this.setPosition(this.getX(), this.getY() + this.acceleration, this.getZ());


            this.setPitch(this.getPitch() + component.getPitchIncrement());
            this.setYaw(this.getYaw() + component.getYawIncrement());
        } else {
//            this.acceleration += this.getAccelerationFactor();
//            this.pos = new Vec3d(this.getX(), this.getY() + this.acceleration, this.getZ());

//            this.setPitch(this.getPitch() + component.getPitchIncrement());
//            this.setYaw(this.getYaw() + component.getYawIncrement());
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
