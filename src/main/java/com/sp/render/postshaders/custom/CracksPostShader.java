package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.PrevUniforms;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CracksPostShader extends PostShader {
    public static final Identifier CRACKS_HOLE_POST = DestroyingMinecraft.idOf("cracks");
    public static final Identifier CRACKS_HOLE_SHADER = DestroyingMinecraft.idOf("cracks/cracks");

    public CracksPostShader() {
        super(CRACKS_HOLE_POST, CRACKS_HOLE_SHADER, null);
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {

    }
}
