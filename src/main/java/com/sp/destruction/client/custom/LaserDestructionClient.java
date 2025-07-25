package com.sp.destruction.client.custom;

import com.sp.destruction.client.ClientDestructionEvent;
import foundry.veil.api.client.render.shader.program.ShaderProgram;

public class LaserDestructionClient extends ClientDestructionEvent {

    public LaserDestructionClient() {
        super(10);
    }

    @Override
    public void setUniforms(ShaderProgram shaderProgram, float tickDelta) {

    }
}
