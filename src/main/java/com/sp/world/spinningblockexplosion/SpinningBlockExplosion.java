package com.sp.world.spinningblockexplosion;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Vector;

public abstract class SpinningBlockExplosion {
    protected final Vec3d position;
    protected boolean explode;
    protected int progress;

    protected final Random random = Random.create();
    private static final Vector<SpinningBlockExplosion> explosions = new Vector<>();

    SpinningBlockExplosion(Vec3d position){
        this.position = position;
        explosions.add(this);
    }

    public void explode(World world){
        if(!explode) return;
//        this.progress++;
    }

    public void beginExplosion() {
        this.explode = true;
    }

    public static synchronized Vector<SpinningBlockExplosion> getExplosions(){
        return (Vector<SpinningBlockExplosion>) explosions.clone();
    }

    public static synchronized void removeExplosion(SpinningBlockExplosion explosion){
        explosions.remove(explosion);
    }
}
