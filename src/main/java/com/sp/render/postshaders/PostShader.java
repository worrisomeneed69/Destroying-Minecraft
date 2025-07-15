package com.sp.render.postshaders;

import com.sp.destruction.client.ClientDestructionEvent;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Vector;

/**
 * This class is used to keep all the shaders and their uniforms organized<br><br>
 * If a shader doesn't need any uniforms to be set then there really is no point in instantiating a new Post Shader
 */
@Environment(EnvType.CLIENT)
public abstract class PostShader {
    protected final Identifier POST;
    protected final Identifier SHADER;
    protected final ClientDestructionEvent clientDestructionEvent;
    private static final Vector<PostShader> allInstances = new Vector<>();

    public PostShader(Identifier post, Identifier shader, @Nullable ClientDestructionEvent clientDestructionEvent){
        this.POST = post;
        this.SHADER = shader;
        this.clientDestructionEvent = clientDestructionEvent;
        allInstances.add(this);
    }

    public void setUniforms(PostPipeline.Context context, float tickDelta, MinecraftClient client, World clientWorld) {
        ShaderProgram shaderProgram = context.getShader(this.getShader());
        if (shaderProgram != null) {
            this.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
        }
    }

    /**
     * Most of the time the shaders will have a destruction event that already sets the uniforms.<br><br>
     * If a shader doesn't have a destruction event, you can override this method and add any uniforms you want
     */
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        if(this.clientDestructionEvent != null) {
            this.clientDestructionEvent.setUniforms(shaderProgram, tickDelta);
        }
    }

    public Identifier getShader() {
        return SHADER;
    }
    public Identifier getPost() {
        return POST;
    }
    public ClientDestructionEvent getDestructionEvent() {
        return clientDestructionEvent;
    }

    public static synchronized Vector<PostShader> getAllInstances() {
        return (Vector<PostShader>) allInstances.clone();
    }

}
