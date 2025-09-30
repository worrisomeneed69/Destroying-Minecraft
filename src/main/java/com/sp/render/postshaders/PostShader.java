package com.sp.render.postshaders;

import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.destruction.client.ClientDestructionEvents;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Interface;

/**
 * This class is used to keep all the shaders and their uniforms organized<br><br>
 * If a shader doesn't need any uniforms to be set then there really is no point in instantiating a new Post Shader
 */
@Environment(EnvType.CLIENT)
public abstract class PostShader {
    protected final Identifier POST;
    protected final Identifier SHADER;
    protected DestructionUniformCallback destructionUniformCallback;


    public PostShader(Identifier post, Identifier shader){
        this.POST = post;
        this.SHADER = shader;
    }

    public void setUniformCallback(DestructionUniformCallback destructionUniformCallback) {
        this.destructionUniformCallback = destructionUniformCallback;
    }

    public void setUniforms(PostPipeline.Context context, float tickDelta, MinecraftClient client, World clientWorld) {
        ShaderProgram shaderProgram = context.getShader(this.SHADER);
        if (shaderProgram != null) {
            this.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
        }
    }

    /**
     * Most of the time the shaders will have a destruction event that already sets the uniforms.<br><br>
     * If a shader doesn't have a destruction event, you can override this method and add any uniforms you want
     */
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        if (this.destructionUniformCallback != null) {
            this.destructionUniformCallback.setUniforms(shaderProgram, tickDelta);
        }
    }

    public Identifier getShader() {
        return SHADER;
    }
    public Identifier getPost() {
        return POST;
    }

    public interface DestructionUniformCallback {
        void setUniforms(ShaderProgram shaderProgram, float tickDelta);
    }

}
