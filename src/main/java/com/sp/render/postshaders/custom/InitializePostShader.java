package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.client.custom.InitializeDestructionClient;
import com.sp.render.postshaders.PostShader;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class InitializePostShader extends PostShader {
    public static final Identifier POST = DestroyingMinecraft.idOf("initialize");
    public static final Identifier SHADER = DestroyingMinecraft.idOf("initialize/initialize");

    public InitializePostShader() {
        super(POST, SHADER, new InitializeDestructionClient());
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        super.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
    }
}
