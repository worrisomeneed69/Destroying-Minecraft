package com.sp.render.rendertimers.planet;

import com.sp.render.rendertimers.ExplosionRenderTimer;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;

public class PlanetRenderTimer extends ExplosionRenderTimer {
    private static final ShaderTimer planetFallTimer = new ShaderTimer();

    public PlanetRenderTimer(int duration) {
        super(duration, "planet", "planet/planet");
    }

    @Override
    public void updateTimer() {
        if(this.enable) {
            this.progress++;
            if (this.progress <= this.duration) {
                planetFallTimer.setTimer(Easing.LINEAR.ease((float) this.progress / this.duration));
            }

            //prevTimer = timer;
            planetFallTimer.setPrevTimer();
        } else {
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        planetFallTimer.reset();
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "planetFallTimer", planetFallTimer.getTimer(tickDelta));
    }
}
