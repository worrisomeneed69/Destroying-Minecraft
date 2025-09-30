package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.postshaders.PostShader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CracksPostShader extends PostShader {
    public static final Identifier CRACKS_HOLE_POST = DestroyingMinecraft.idOf("cracks");
    public static final Identifier CRACKS_HOLE_SHADER = DestroyingMinecraft.idOf("cracks/cracks");

    public CracksPostShader() {
        super(CRACKS_HOLE_POST, CRACKS_HOLE_SHADER);
    }
}
