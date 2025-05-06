package com.sp.render.rendertimers.nuke;

import com.sp.render.rendertimers.ExplosionRenderTimer;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class NukeRenderTimer extends ExplosionRenderTimer {
    private static final ShaderTimer smokeRiseTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();

    public NukeRenderTimer(int duration) {
        super(duration);
    }

    @Override
    public void updateTimer() {
        if(this.enable) {
            this.progress++;
            if (this.progress <= this.duration) {
                smokeRiseTimer.setTimer(Easing.EASE_OUT_SINE.ease((float) this.progress / this.duration));
                flashTimer.setTimer(Easing.EASE_OUT_SINE.ease(Math.min((float) this.progress / this.duration*1.25f, 1.0f)));
            }

            //prevTimer = timer;
            smokeRiseTimer.setPrevTimer();
            flashTimer.setPrevTimer();
        } else {
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        smokeRiseTimer.reset();
        flashTimer.reset();
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
//        if(this.enable) {
            BetterUniforms.setFloat(shaderProgram, "smokeRiseTimer", smokeRiseTimer.getTimer(tickDelta));
            BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
//        }
    }
}
