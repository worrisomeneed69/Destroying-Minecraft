package com.sp.render.rendertimers;

import foundry.veil.api.client.render.shader.program.ShaderProgram;

/**
 * This class is used to update the explosion in any way and sends that data into the shader's uniforms
 */
public abstract class ExplosionRenderTimer {
    protected boolean enable;
    protected int progress = 0;
    protected final int duration;

    protected ExplosionRenderTimer(int duration) {
        this.duration = duration;
    }

    public abstract void updateTimer();

    public void toggleExplosion(boolean on){
        this.enable = on;
    }

    public void resetExplosionTimer(){
        this.progress = 0;
    }

    public abstract void setUniforms(ShaderProgram shaderProgram, float tickDelta);
}
