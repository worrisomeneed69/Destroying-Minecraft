package com.sp.world.spinningblockexplosion;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Vector;

public abstract class SpinningBlockExplosion {
    protected final Vec3d position;
    protected boolean explode;
    protected int progress;
    protected ServerWorld world;

    protected final Random random = Random.create();
    private static final Vector<SpinningBlockExplosion> explosions = new Vector<>();

    public SpinningBlockExplosion(Vec3d position) {
        this.position = position;
        explosions.add(this);
    }

    public void explode() {
        if(!explode) return;
//        this.progress++;
    }

    public void beginExplosion(ServerWorld world) {
        this.world = world;
        this.explode = true;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public static synchronized Vector<SpinningBlockExplosion> getExplosions() {
        return (Vector<SpinningBlockExplosion>) explosions.clone();
    }

    public static synchronized void removeExplosion(SpinningBlockExplosion explosion) {
        explosions.remove(explosion);
    }
}
