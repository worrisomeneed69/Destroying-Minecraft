package com.sp.world.playzone;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayZone {
    private final Box boundingBox;
    private static final AtomicInteger CURRENT_ID = new AtomicInteger();
    private int id;

    public PlayZone(BlockPos position1, BlockPos position2) {
        this(Box.enclosing(position1, position2));
    }

    //For client side only
    public PlayZone(Box box, int id) {
        this.boundingBox = box;
        this.id = id;
    }

    public PlayZone(Box box) {
        this.boundingBox = box;
        this.id = CURRENT_ID.incrementAndGet();
    }

    public boolean isPositionInsideZone(Vec3d pos) {
        return boundingBox.contains(pos);
    }

    public int getId() {
        return this.id;
    }

    public Box getBoundingBox() {
        return boundingBox;
    }

}
