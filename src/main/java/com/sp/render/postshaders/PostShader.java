package com.sp.render.postshaders;

import com.sp.render.rendertimers.ExplosionRenderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Vector;

/**
 * This class is used to keep all the shaders and their uniforms organized
 */
public abstract class PostShader {
    protected final Identifier POST;
    protected final Identifier SHADER;
    protected final ExplosionRenderTimer renderTimer;
    private static final Vector<PostShader> allInstances = new Vector<>();

    public PostShader(Identifier post, Identifier shader, @Nullable ExplosionRenderTimer renderTimer){
        this.POST = post;
        this.SHADER = shader;
        this.renderTimer = renderTimer;
        allInstances.add(this);
    }

    public void setUniforms(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        if(this.renderTimer != null) {
            this.renderTimer.setUniforms(shaderProgram, tickDelta);
        }
    }

    public Identifier getShader() {
        return SHADER;
    }
    public Identifier getPost() {
        return POST;
    }
    public ExplosionRenderTimer getRenderTimer() {
        return renderTimer;
    }

    public static synchronized Vector<PostShader> getAllInstances() {
        return (Vector<PostShader>) allInstances.clone();
    }

}
