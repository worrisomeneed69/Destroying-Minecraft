package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import com.sp.render.rendertimers.nuke.NukeRenderTimer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NukePostShader extends PostShader {
    public static final Identifier NUKE_POST = DestroyingMinecraft.idOf("nuke");
    public static final Identifier NUKE_SHADER = DestroyingMinecraft.idOf("nuke/nuke");

    public NukePostShader() {
        super(NUKE_POST, NUKE_SHADER, new NukeRenderTimer(400));
    }
}
