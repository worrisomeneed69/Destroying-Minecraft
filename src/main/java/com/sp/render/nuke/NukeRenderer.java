package com.sp.render.nuke;

import com.sp.render.ExplosionRenderer;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class NukeRenderer extends ExplosionRenderer {
    private static final ShaderTimer smokeRiseTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();

    public NukeRenderer(int duration) {
        super(duration, "nuke", "nuke/nuke");
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
