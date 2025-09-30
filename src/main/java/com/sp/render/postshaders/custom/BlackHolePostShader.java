package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.render.PrevUniforms;
import com.sp.render.postshaders.PostShader;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class BlackHolePostShader extends PostShader {
    public static final Identifier BLACK_HOLE_POST = DestroyingMinecraft.idOf("black_hole");
    public static final Identifier BLACK_HOLE_SHADER = DestroyingMinecraft.idOf("blackhole/black_hole");

    public BlackHolePostShader() {
        super(BLACK_HOLE_POST, BLACK_HOLE_SHADER);
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        super.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
        if (PrevUniforms.isInitialized()) {
            BetterUniforms.setMatrix(shaderProgram, "prevProjMat", PrevUniforms.getPrevProjMat());
            BetterUniforms.setMatrix(shaderProgram, "prevViewMat", PrevUniforms.getPrevModelViewMat());
            BetterUniforms.setVector3f(shaderProgram, "prevCameraPos", PrevUniforms.getPrevCameraPos());
        }

        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(clientWorld);
        float redMultiplier = (float) Math.max(1.0f - component.getGravityLerp(), 0.01f);
        BetterUniforms.setVector3f(shaderProgram, "redMultiplier", new Vector3f(1.0f, redMultiplier, redMultiplier));

        PrevUniforms.update();
    }
}
