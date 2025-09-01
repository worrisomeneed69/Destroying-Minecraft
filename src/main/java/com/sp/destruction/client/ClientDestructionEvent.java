package com.sp.destruction.client;

import com.sp.destruction.DestructionEvent;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Vector;

/**
 * Handles all destruction events client side, including passing uniforms to the shaders
 */
public abstract class ClientDestructionEvent extends DestructionEvent {
    private static final Vector<DestructionEvent> clientInstances = new Vector<>();

    public ClientDestructionEvent(int duration) {
        super(duration, true);
        clientInstances.add(this);
    }

    /**
     * Shouldn't skip keyframes client side
     */
    @Override
    protected void skipKeyframe() {

    }

    public abstract void setUniforms(ShaderProgram shaderProgram, float tickDelta);

    public static synchronized Vector<DestructionEvent> getAllClientInstances() {
        return (Vector<DestructionEvent>) clientInstances.clone();
    }
}
