package com.sp.world.spinningblockexplosion;

import com.sp.entity.ModEntities;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectionalSpinningBlockExplosion extends SpinningBlockExplosion {
    private final int length;
    private final int width;
    private final float angle;
    private final float blockDensity;
    private int delay;

    public DirectionalSpinningBlockExplosion(int length, int width, float angle, float blockDensity, Vec3d centerPos) {
        super(centerPos);
        this.length = length;
        this.width = width;
        this.angle = angle;
        this.blockDensity = blockDensity;
    }


    @Override
    public void explode(World world) {
        super.explode(world);
        if(delay <= 0) {
            if (this.progress > length * 2) {
                this.explode = false;
                SpinningBlockExplosion.removeExplosion(this);
            }


            for (int x = -width; x < width; x++) {
                if(this.random.nextFloat() < blockDensity) {
                    SpinningBlockEntity spinningBlockEntity = ModEntities.SPINNING_BLOCK.create(world);
                    if (spinningBlockEntity == null) return;

                    Vec3d newBlockPos = new Vec3d(x, -5, this.progress - length*2).rotateY((float) Math.toRadians(this.angle));

                    spinningBlockEntity.refreshPositionAndAngles(this.position.add(newBlockPos), 0, 0);

                    world.spawnEntity(spinningBlockEntity);
                }
            }
            this.delay = 2;
            this.progress++;
        } else {
            this.delay--;
        }

    }
//2 5 8 11 14
//3x - 1
}