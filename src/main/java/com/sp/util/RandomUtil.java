package com.sp.util;

import net.minecraft.util.math.random.Random;

public class RandomUtil {

    public static float nextBetween(Random random, float min, float max){
        return min + random.nextFloat() * (max - min);
    }

}
