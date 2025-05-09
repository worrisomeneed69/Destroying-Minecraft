package com.sp.world.spinningblockexplosion.custom;

import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectionalSBE extends SpinningBlockExplosion {
    private final int length;
    private final int width;
    private final float angle;
    private final float blockDensity;
    private int delay;

    public DirectionalSBE(int length, int width, float angle, float blockDensity, Vec3d centerPos) {
        super(centerPos);
        this.length = length;
        this.width = width;
        this.angle = angle;
        this.blockDensity = blockDensity;
    }


    @Override
    public void explode(World world) {
        super.explode(world);
        if(this.delay <= 0) {
            if (this.progress > length * 2) {
                this.explode = false;
                SpinningBlockExplosion.removeExplosion(this);
            }


            for (int x = -width; x < width; x++) {
                if(this.random.nextFloat() < blockDensity) {

                }
            }
            this.delay = 0;
            this.progress++;
        } else {
            this.delay--;
        }

    }
//2 5 8 11 14
//3x - 1
}