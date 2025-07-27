package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.client.custom.LaserDestructionClient;
import com.sp.render.postshaders.PostShader;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CracksPostShader extends PostShader {
    public static final Identifier CRACKS_HOLE_POST = DestroyingMinecraft.idOf("cracks");
    public static final Identifier CRACKS_HOLE_SHADER = DestroyingMinecraft.idOf("cracks/cracks");

    public CracksPostShader() {
        super(CRACKS_HOLE_POST, CRACKS_HOLE_SHADER, new LaserDestructionClient());
    }
}
