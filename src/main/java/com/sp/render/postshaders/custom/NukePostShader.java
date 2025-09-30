package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class NukePostShader extends PostShader {
    public static final Identifier NUKE_POST = DestroyingMinecraft.idOf("nuke");
    public static final Identifier NUKE_SHADER = DestroyingMinecraft.idOf("nuke/nuke");

    public NukePostShader() {
        super(NUKE_POST, NUKE_SHADER);
        //new NukeRenderTimer(100)
    }
}
