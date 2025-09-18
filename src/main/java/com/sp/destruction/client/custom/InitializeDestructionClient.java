package com.sp.destruction.client.custom;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.util.BetterUniforms;
import com.sp.util.timer.ShaderTimer;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.world.World;

public class InitializeDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer initTimer = new ShaderTimer();
    private static final ShaderTimer skyTimer = new ShaderTimer();

    public InitializeDestructionClient() {
        super(250);
    }

    @Override
    public void resetEvent() {
        super.resetEvent();
        initTimer.reset();
        skyTimer.reset();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "initTimer", initTimer.getTimer(tickDelta));
        BetterUniforms.setFloat(shaderProgram, "skyTimer", skyTimer.getTimer(tickDelta));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation.KeyframeAnimationBuilder(
                this.duration,

                new Keyframe(0.0, (globalTime, localTime) -> {
                    initTimer.setTimer((float) localTime);
                }),

                new Keyframe(200.0 / this.duration, (globalTime, localTime) -> {
                    initTimer.maxTimer();
                    skyTimer.setTimer((float) localTime);
                }),

                new Keyframe(220.0 / this.duration, () -> {
                    skyTimer.maxTimer();
                })
        ).endAction(() -> {
            initTimer.maxTimer();
            skyTimer.maxTimer();
        }).build();
    }
}
