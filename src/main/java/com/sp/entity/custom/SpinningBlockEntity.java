package com.sp.entity.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class SpinningBlockEntity extends Entity {
    private final SpinningBlockComponent component;

    public SpinningBlockEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.component = InitializeComponents.SPINNING_BLOCK.get(this);
    }

    private SpinningBlockEntity(World world, BlockState state) {
        this(ModEntities.SPINNING_BLOCK, world);
        this.component.setBlockState(state);
    }

    public static SpinningBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state){
        SpinningBlockEntity entity = new SpinningBlockEntity(world, state);
        entity.setPosition(pos.toCenterPos());
        world.spawnEntity(entity);

        return entity;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
//        SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(this);

//        this.acceleration += component.getAccelerationFactor()*0.1f;
//        Vec3d randDir = new Vec3d(component.getRandDir());
//        this.setVelocity(randDir);


        if(!this.isOnGround()) {
            this.setVelocity(this.getVelocity().add(new Vec3d(0, -0.07, 0)));

            this.move(MovementType.SELF, this.getVelocity());
        }

        if(!this.getWorld().isClient) {
            if(this.age > 100){
                this.discard();
            }
        }

    }

    public SpinningBlockComponent getComponent() {
        return this.component;
    }

    @Override
    protected double getGravity() {
        return 0.1;
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
