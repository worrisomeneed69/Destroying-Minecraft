package com.sp.destruction.client;

import com.sp.destruction.DestructionType;
import com.sp.destruction.DestructionEvent;
import com.sp.render.postshaders.PostShader;
import foundry.veil.api.client.render.shader.program.ShaderProgram;

import java.util.Vector;

/**
 * Handles all destruction events client side, including passing uniforms to the shaders
 */
public abstract class ClientDestructionEvent extends DestructionEvent {
    private static final Vector<DestructionEvent> clientInstances = new Vector<>();
    private final PostShader postShader;

    public ClientDestructionEvent(DestructionType destructionType, PostShader postShader, int duration) {
        super(destructionType, duration, true);
        clientInstances.add(this);
        this.postShader = postShader;
        this.postShader.setUniformCallback(this::setUniforms);
    }

    /**
     * Shouldn't skip keyframes client side
     */
    @Override
    protected void skipKeyframe() {

    }

    public PostShader getPostShader() {
        return this.postShader;
    }

    public abstract void setUniforms(ShaderProgram shaderProgram, float tickDelta);

    public static <T extends ClientDestructionEvent> T register(T event) {
        clientInstances.add(event);
        return event;
    }

    public static synchronized Vector<DestructionEvent> getAllClientInstances() {
        return (Vector<DestructionEvent>) clientInstances.clone();
    }
}
