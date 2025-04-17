package com.sp.render.supernova;

import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class SupernovaRenderer {
    private static final ShaderTimer implodeTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static final ShaderTimer explosionTimer = new ShaderTimer();
    private static int progress = 0;
    private static final int duration = 100;

    public static void updateSupernovaTimer() {
        progress++;
        if(progress < duration){
            implodeTimer.setTimer(Easing.EASE_IN_CUBIC.ease((float) progress /duration));

        } else {
            implodeTimer.maxTimer();
            flashTimer.setTimer(Easing.EASE_IN_OUT_CUBIC.ease((float) (progress - duration) / (duration*1.5f)));
            explosionTimer.setTimer((float) (progress - duration) / (duration*3f));

        }

        implodeTimer.setLastTimer();
        flashTimer.setLastTimer();
        explosionTimer.setLastTimer();
    }

    public static void resetSupernovaTimer(){
        implodeTimer.reset();
        flashTimer.reset();
        explosionTimer.reset();
        progress = 0;
    }

    public static void setSupernovaUniforms(ShaderProgram shaderProgram, float tickDelta){
        shaderProgram.setFloat("supernovaTimer", implodeTimer.getTimer(tickDelta));
        shaderProgram.setFloat("flashTimer", flashTimer.getTimer(tickDelta));
        shaderProgram.setFloat("explosionTimer", explosionTimer.getTimer(tickDelta));
    }

}
