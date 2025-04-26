package com.sp.render.rendertimers.supernova;

import com.sp.render.rendertimers.ExplosionRenderTimer;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class SupernovaRenderTimer extends ExplosionRenderTimer {
    private static final ShaderTimer implodeTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private static final ShaderTimer explosionTimer = new ShaderTimer();

    public SupernovaRenderTimer(int duration) {
        super(duration, "sky", "sky/sky");
    }

    @Override
    public void updateTimer() {
        if(this.enable) {
            this.progress++;
            if (this.progress < this.duration) {
                //Sun implosion
                implodeTimer.setTimer(Easing.EASE_IN_CUBIC.ease((float) this.progress / this.duration));
            } else {
                //Flash, then fade to supernova
                implodeTimer.maxTimer();
                flashTimer.setTimer(Easing.EASE_IN_OUT_CUBIC.ease((float) (this.progress - this.duration) / (this.duration * 1.5f)));
                explosionTimer.setTimer((float) (this.progress - this.duration) / (this.duration * 3f));

            }

            //prevTimer = timer;
            implodeTimer.setPrevTimer();
            flashTimer.setPrevTimer();
            explosionTimer.setPrevTimer();
        } else {
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        implodeTimer.reset();
        flashTimer.reset();
        explosionTimer.reset();
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
//        if(this.enable) {
            BetterUniforms.setFloat(shaderProgram, "supernovaTimer", implodeTimer.getTimer(tickDelta));
            BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
            BetterUniforms.setFloat(shaderProgram, "explosionTimer", explosionTimer.getTimer(tickDelta));
//        }
    }
}
