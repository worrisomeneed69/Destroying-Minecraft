package com.sp.mixin;

import foundry.veil.Veil;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.vertex.VeilVertexFormat;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiFunction;

import static net.minecraft.client.render.RenderLayer.CUTOUT_BUFFER_SIZE;
import static net.minecraft.client.render.RenderPhase.*;

@Mixin(VeilRenderType.class)
public class QuasarFix {

    @Shadow
    @Final
    private static RenderPhase.ShaderProgram PARTICLE;
    @Shadow
    @Final
    private static RenderPhase.ShaderProgram PARTICLE_ADDITIVE;


    @Shadow
    private static final BiFunction<Identifier, Boolean, RenderLayer> QUASAR_PARTICLE = Util.memoize((texture, additive) -> {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
                .program(additive ? PARTICLE_ADDITIVE : PARTICLE)
                .texture(new RenderPhase.Texture(texture, false, false))
                .transparency(additive ? ADDITIVE_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)
                .lightmap(ENABLE_LIGHTMAP)
                .writeMaskState(ALL_MASK) //Bruh this is literally it
                .build(false);
        return RenderLayer.of(Veil.MODID + ":quasar_particle", VeilVertexFormat.QUASAR_PARTICLE, VertexFormat.DrawMode.QUADS, CUTOUT_BUFFER_SIZE, false, !additive, state);
    });

}
