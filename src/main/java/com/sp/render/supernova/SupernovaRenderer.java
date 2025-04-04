package com.sp.render.supernova;

import foundry.veil.api.client.util.Easing;

public class SupernovaRenderer {
    private static float sunSize;
    private static float lastSupernovaTimer = 0.0f;
    private static float supernovaTimer = 0.0f;
    private static float lastFlash = 0.998f;
    private static float flash = 0.998f;
    private static int progress = 0;
    private static int duration = 100;

    public static void updateSupernovaTimer() {
        progress++;
        if(progress < duration){
            supernovaTimer = Easing.EASE_IN_EXPO.ease((float) progress /duration);
            flash = 0.998f;
        } else {
            supernovaTimer = 1.0F;
            flash = (float) (progress - duration) / (duration);
        }

        lastSupernovaTimer = supernovaTimer;
        lastFlash = flash;
    }

    public static float getSupernovaTimer(float tickDelta) {
        return lastSupernovaTimer + (supernovaTimer - lastSupernovaTimer) * tickDelta;
    }

    public static float getFlash(float tickDelta) {
        return lastFlash + (flash - lastFlash) * tickDelta;
    }

    public static void resetSupernovaTimer(){
        supernovaTimer = 0.0f;
        progress = 0;
    }

}
