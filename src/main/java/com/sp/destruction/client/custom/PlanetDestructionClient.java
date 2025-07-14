package com.sp.destruction.client.custom;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.util.BetterUniforms;
import com.sp.util.ShaderTimer;
import com.sp.util.keyframes.KeyframeAnimation;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PlanetDestructionClient extends ClientDestructionEvent {
    private static final ShaderTimer planetFallTimer = new ShaderTimer();

    public PlanetDestructionClient() {
        super(1800);
    }

    @Override
    protected void resetEvent() {
        planetFallTimer.reset();
        super.resetEvent();
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {
        BetterUniforms.setFloat(shaderProgram, "planetFallTimer", planetFallTimer.getTimer(tickDelta));
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return super.initAnimations(world);
    }
}
