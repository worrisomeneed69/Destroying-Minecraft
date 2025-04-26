package com.sp.render.rendertimers;

import com.sp.DestroyingMinecraft;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.util.Identifier;

public abstract class ExplosionRenderTimer {
    protected boolean enable;
    protected int progress = 0;
    protected final int duration;
    public final Identifier POST;
    public final Identifier SHADER;

    protected ExplosionRenderTimer(int duration, String postDirectory, String shaderDirectory){
        this.POST = DestroyingMinecraft.idOf(postDirectory);
        this.SHADER = DestroyingMinecraft.idOf(shaderDirectory);
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
