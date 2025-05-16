package com.sp.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

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

}
