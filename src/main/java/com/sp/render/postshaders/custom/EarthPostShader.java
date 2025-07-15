package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EarthPostShader extends PostShader {
    public static final Identifier POST = DestroyingMinecraft.idOf("earth");
    public static final Identifier SHADER = DestroyingMinecraft.idOf("earth/earth");

    public EarthPostShader() {
        super(POST, SHADER, null);
    }
}
