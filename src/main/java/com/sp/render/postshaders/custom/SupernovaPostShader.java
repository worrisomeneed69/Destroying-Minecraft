package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SupernovaPostShader extends PostShader {
    public static final Identifier SUPERNOVA_POST = DestroyingMinecraft.idOf("sky");
    public static final Identifier SUPERNOVA_SHADER = DestroyingMinecraft.idOf("sky/sky");

    public SupernovaPostShader() {
        super(SUPERNOVA_POST, SUPERNOVA_SHADER);
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        Optional<Matrix4f> matrix4f = ShadowMapRenderer.getShadowViewMat();

        if (matrix4f.isPresent()) {
            BetterUniforms.setMatrix(shaderProgram, "sunMat", matrix4f.get().invert());
        }


        super.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
    }
}
