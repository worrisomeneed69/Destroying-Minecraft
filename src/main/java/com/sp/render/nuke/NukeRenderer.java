package com.sp.render.nuke;

import com.sp.render.ExplosionRenderer;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class NukeRenderer extends ExplosionRenderer {
    private static final ShaderTimer smokeRiseTimer = new ShaderTimer();

    NukeRenderer(int duration) {
        super(duration);
    }

    @Override
    public void updateTimer() {
        if(this.enable) {
            this.progress++;
            if (this.progress < this.duration) {
                //Sun implosion
                smokeRiseTimer.setTimer(Easing.EASE_IN_CUBIC.ease((float) this.progress / this.duration));
            }

            //prevTimer = timer;
            smokeRiseTimer.setPrevTimer();
        } else {
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        smokeRiseTimer.reset();
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "smokeRiseTimer", smokeRiseTimer.getTimer(tickDelta));
    }
}
