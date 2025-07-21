package com.sp.render.postshaders.custom;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.client.custom.blackhole.BlackHoleDestructionClientPart1;
import com.sp.destruction.client.custom.blackhole.BlackHoleDestructionClientPart2;
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
    public final BlackHoleDestructionClientPart2 destruction2 = new BlackHoleDestructionClientPart2();

    public BlackHolePostShader() {
        super(BLACK_HOLE_POST, BLACK_HOLE_SHADER, new BlackHoleDestructionClientPart1());
    }

    @Override
    public void setUniformsForShader(ShaderProgram shaderProgram, float tickDelta, MinecraftClient client, World clientWorld) {
        super.setUniformsForShader(shaderProgram, tickDelta, client, clientWorld);
        if (PrevUniforms.isInitialized()) {
            BetterUniforms.setMatrix(shaderProgram, "prevProjMat", PrevUniforms.getPrevProjMat());
            BetterUniforms.setMatrix(shaderProgram, "prevViewMat", PrevUniforms.getPrevModelViewMat());
            BetterUniforms.setVector(shaderProgram, "prevCameraPos", PrevUniforms.getPrevCameraPos());
        }

        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(clientWorld);
        float redMultiplier = (float) Math.max(1.0f - component.getGravityLerp(), 0.01f);
        BetterUniforms.setVector(shaderProgram, "redMultiplier", new Vector3f(1.0f, redMultiplier, redMultiplier));

        PrevUniforms.update();
    }
}
