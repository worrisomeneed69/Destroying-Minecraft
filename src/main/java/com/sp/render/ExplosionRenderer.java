package com.sp.render;

import foundry.veil.api.client.render.shader.program.ShaderProgram;

public abstract class ExplosionRenderer {
    protected boolean enable;
    protected int progress = 0;
    protected final int duration;

    protected ExplosionRenderer(int duration){
        this.duration = duration;
    }

    public abstract void updateTimer();

    public void toggleExplosion(boolean on){
        this.enable = on;
    }

    public void resetExplosionTimer(){
        this.progress = 0;
    };

    public abstract void setUniforms(ShaderProgram shaderProgram, float tickDelta);
}
