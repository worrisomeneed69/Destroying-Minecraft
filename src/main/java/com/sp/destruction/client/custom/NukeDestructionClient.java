package com.sp.destruction.client.custom;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NukeDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer smokeRiseTimer = new ShaderTimer();
    private static final ShaderTimer flashTimer = new ShaderTimer();
    private long startTime = -1L;
    private float floatProgress;

    public NukeDestructionClient() {
        super(100);
    }

    @Override
    public void tick(World world) {
        if(this.active) {
            if(this.startTime == -1L) this.startTime = world.getTime();

            if(world.getTime() < this.startTime){
                this.resetEvent();
            }

            this.floatProgress = (float) Math.min((double) (world.getTime() - this.startTime) / this.duration, 1.0);
            if (this.floatProgress <= this.duration) {
                smokeRiseTimer.setTimer(Easing.EASE_OUT_SINE.ease(this.floatProgress));
                flashTimer.setTimer(Easing.EASE_OUT_SINE.ease(Math.min(this.floatProgress*2.75f, 1.0f)));
            }

        } else {
            this.resetEvent();
        }
    }

    @Override
    public void resetEvent() {
        smokeRiseTimer.reset();
        flashTimer.reset();
        this.startTime = -1;
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "smokeRiseTimer", smokeRiseTimer.getTimer(tickDelta));
//        System.out.println(smokeRiseTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "flashTimer", flashTimer.getTimer(tickDelta));
    }
}
