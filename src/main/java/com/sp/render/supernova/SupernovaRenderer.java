package com.sp.render.supernova;

import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class SupernovaRenderer {
    private static final ShaderTimer implodeTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static final ShaderTimer explosionTimer = new ShaderTimer();
    private static int progress = 0;
    private static final int duration = 100;
    private static boolean enable;

    public static void updateSupernovaTimer() {
        if(enable) {
            progress++;
            if (progress < duration) {
                //Sun implosion
                implodeTimer.setTimer(Easing.EASE_IN_CUBIC.ease((float) progress / duration));
            } else {
                //Flash, then fade to supernova
                implodeTimer.maxTimer();
                flashTimer.setTimer(Easing.EASE_IN_OUT_CUBIC.ease((float) (progress - duration) / (duration * 1.5f)));
                explosionTimer.setTimer((float) (progress - duration) / (duration * 3f));

            }

            //prevTimer = timer;
            implodeTimer.setPrevTimer();
            flashTimer.setPrevTimer();
            explosionTimer.setPrevTimer();
        } else {
            resetSupernovaTimer();
        }
    }

    public static void toggleSupernova(boolean on){
        enable = on;
    }

    public static void resetSupernovaTimer() {
        implodeTimer.reset();
        flashTimer.reset();
        explosionTimer.reset();
        progress = 0;
    }

    public static void setSupernovaUniforms(ShaderProgram shaderProgram, float tickDelta){
        BetterUniforms.setFloat(shaderProgram, "supernovaTimer", implodeTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "explosionTimer", explosionTimer.getTimer(tickDelta));
    }

}
