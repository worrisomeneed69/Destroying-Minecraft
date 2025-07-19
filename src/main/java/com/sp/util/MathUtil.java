package com.sp.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

import static java.lang.Math.floor;

public class MathUtil {
    private static final Random random = Random.create();

    /**
     * Selects a random object from a list
     * @param list The list to select from
     * @return A random object from the list
     */
    public static <E> E randomValueInList(List<E> list) {
        return list.get(random.nextBetween(0, list.size() - 1));
    }

    public static <E> E randomValueInList(E[] array) {
        return array[random.nextBetween(0, array.length - 1)];
    }

    public static float nextBetween(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Framerate Independent lerp
     * <a href="https://www.youtube.com/watch?v=LSNQuFEDOyQ">Learned from here</a>
     */
    public static float Lerp(float source, float destination, float smoothingFactor, float delta) {
        return MathHelper.lerp(1.0f - (float) Math.pow(smoothingFactor, delta), source, destination);
    }

    public static Vec3d getCenterPos(List<BlockPos> blocks) {
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;

        for (BlockPos blockPos : blocks) {
            sumX += blockPos.getX();
            sumY += blockPos.getY();
            sumZ += blockPos.getZ();
        }
        int count = blocks.size();

        sumX /= count;
        sumY /= count;
        sumZ /= count;

        return new Vec3d(sumX + 0.5, sumY + 0.5, sumZ + 0.5);
    }

    public static Vector3d toVector3d(Vec3d vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vec3d toVec3d(Vector3d vec) {
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    public static Vec3d toVec3d(Vector3f vec) {
        return new Vec3d(vec.x, vec.y, vec.z);
    }

}
