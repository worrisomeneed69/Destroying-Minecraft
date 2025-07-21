package com.sp.world.spinningblockexplosion.custom;

import com.sp.DestroyingMinecraft;
import com.sp.entity.custom.SpinningBlockEntity;
import com.sp.world.ModGameRules;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

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
                            SpinningBlockEntity spinningBlockEntity = SpinningBlockEntity.spawnFromBlock(world, mutable, world.getBlockState(mutable));

                            spinningBlockEntity.getComponent().setLifeTime(random.nextBetween(60, 120));
                            world.spawnEntity(spinningBlockEntity);
                            spinningBlockEntity.setVelocity(mutable.toCenterPos().subtract(this.position).normalize());
                            spinningBlockEntity.addVelocityInternal(new Vec3d(0, 1, 0));
                        }

                        if (world.getGameRules().getBoolean(ModGameRules.ALLOW_EXPLOSIONS)) {
                            world.setBlockState(mutable, Blocks.AIR.getDefaultState());
                        }
                    }

                }
            }
        }

        int affectedRadius = Math.max(radius*3, 10);
        List<LivingEntity> nearbyEntitiesList = world.getEntitiesByType(
                TypeFilter.instanceOf(LivingEntity.class),
                new Box(
                        this.position.subtract(affectedRadius, affectedRadius, affectedRadius),
                        this.position.add(affectedRadius, affectedRadius, affectedRadius)
                ),
                LivingEntity::isAlive
        );

        for (LivingEntity entity : nearbyEntitiesList) {
            if (!entity.canTakeDamage()) continue;
            double distanceFromCenter = Math.sqrt(entity.squaredDistanceTo(this.position)) / affectedRadius;
            if (distanceFromCenter > 1.3) continue;  //Also affect players a little bit outside the actual destruction

            Vec3d knockbackVelocity = entity.pos.subtract(this.position).add(0, 0.75, 0).normalize();
            entity.addVelocityInternal(knockbackVelocity.multiply((1.3 - distanceFromCenter) * 2.0f));

            entity.damage(world.getDamageSources().explosion(null), (float) (1.3 - distanceFromCenter) * this.radius);

            if (entity instanceof PlayerEntity player) {
                DestroyingMinecraft.sendPointSBEPacket(player, this.position, affectedRadius/2);
            }

        }

        this.explode = false;
        SpinningBlockExplosion.removeExplosion(this);
    }


}
