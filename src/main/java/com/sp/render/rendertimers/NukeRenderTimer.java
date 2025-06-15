package com.sp.render.rendertimers;

import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;
import net.minecraft.client.world.ClientWorld;

public class NukeRenderTimer extends ExplosionRenderTimer {
    private static final ShaderTimer smokeRiseTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();

    public NukeRenderTimer(int duration) {
        super(duration);
    }

    @Override
    public void updateTimer(ClientWorld clientWorld) {
        if(this.enable) {
            smokeRiseTimer.setPrevTimer();
            flashTimer.setPrevTimer();

            this.updateProgress(clientWorld);
            if (this.progress <= this.duration) {
                smokeRiseTimer.setTimer(Easing.EASE_OUT_SINE.ease(this.progress));
                flashTimer.setTimer(Easing.EASE_OUT_SINE.ease(Math.min(this.progress*2.75f, 1.0f)));
            }

        } else {
            this.resetExplosionTimer();
        }
    }

    @Override
    public void resetExplosionTimer() {
        smokeRiseTimer.reset();
        flashTimer.reset();
        this.startTime = -1;
        super.resetExplosionTimer();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "smokeRiseTimer", smokeRiseTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
    }
}
