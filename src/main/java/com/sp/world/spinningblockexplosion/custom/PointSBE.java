package com.sp.world.spinningblockexplosion.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.ModEntities;
import com.sp.entity.custom.SpinningBlockEntity;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PointSBE extends SpinningBlockExplosion {
    private final int radius;
    private final float density;

    public PointSBE(int radius, float density, Vec3d position) {
        super(position);
        this.radius = radius;
        this.density = density;
    }

    @Override
    public void explode(World world) {
        super.explode(world);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = -this.radius; x < this.radius; x++) {
            for (int y = -this.radius; y < this.radius; y++) {
                for (int z = -this.radius; z < this.radius; z++) {
                    //If it's in the sphere
                    if (mutable.set(this.position.x + x, this.position.y + y, this.position.z + z).isWithinDistance(this.position, this.radius) && world.getBlockState(mutable).isSolid()) {
                        if(this.random.nextFloat() < this.density) {
                            SpinningBlockEntity spinningBlockEntity = ModEntities.SPINNING_BLOCK.create(world);
                            if (spinningBlockEntity == null) return;
                            SpinningBlockComponent component = InitializeComponents.SPINNING_BLOCK.get(spinningBlockEntity);

                            spinningBlockEntity.refreshPositionAndAngles(mutable, 0, 0);
                            component.setBlockState(world.getBlockState(mutable));


                            spinningBlockEntity.setVelocity(mutable.toCenterPos().subtract(this.position).normalize());
//                            spinningBlockEntity.setVelocity(new Vec3d(-1, 0, -1));
                            spinningBlockEntity.addVelocityInternal(new Vec3d(-1, 1, -1));
                            spinningBlockEntity.velocityModified = true;
                            spinningBlockEntity.velocityDirty = true;

                            world.spawnEntity(spinningBlockEntity);
                        }

                        world.setBlockState(mutable, Blocks.AIR.getDefaultState());
                    }

                }
            }
        }

        this.explode = false;
        SpinningBlockExplosion.removeExplosion(this);
    }


}
