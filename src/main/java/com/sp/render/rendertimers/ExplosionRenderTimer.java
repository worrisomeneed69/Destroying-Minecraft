package com.sp.render.rendertimers;

import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.world.ClientWorld;

/**
 * This class is used to update the explosion in any way and sends that data into the shader's uniforms
 */
public abstract class ExplosionRenderTimer {
    protected boolean enable;
    protected long startTime = -1;
    protected float progress = 0;
    protected final int duration;

    protected ExplosionRenderTimer(int duration) {
        this.duration = duration;
    }

    /**
     * The main update loop for each render timer called every tick
     */
    public abstract void updateTimer(ClientWorld clientWorld);

    public void toggleExplosion(boolean on){
        this.enable = on;
    }

    public void resetExplosionTimer(){
        this.progress = 0;
    }

    protected void updateProgress(ClientWorld clientWorld) {
        if(this.startTime == -1) this.startTime = clientWorld.getTime();

        if(clientWorld.getTime() < this.startTime){
            this.enable = false;
        }

        this.progress = Math.min((float) (clientWorld.getTime() - this.startTime) / this.duration, 1.0f);
    }

    public abstract void setUniforms(ShaderProgram shaderProgram, float tickDelta);
}
