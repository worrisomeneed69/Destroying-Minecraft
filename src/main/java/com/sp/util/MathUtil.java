package com.sp.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class MathUtil {

    public static float nextBetween(Random random, float min, float max){
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Framerate Independent lerp
     * <a href="https://www.youtube.com/watch?v=LSNQuFEDOyQ">Learned from here</a>
     */
    public static float Lerp(float source, float destination, float smoothingFactor, float delta){
        return MathHelper.lerp(1.0f - (float) Math.pow(smoothingFactor, delta), source, destination);
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
