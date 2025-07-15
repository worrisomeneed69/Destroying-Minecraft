package com.sp.render;

import com.sp.DestroyingMinecraft;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

import static net.minecraft.client.render.RenderPhase.*;

public class CustomRenderLayersAndVertexFormats {

    /**
     * CUSTOM VERTEX FORMAT ELEMENTS
     */
    //For some reason setting the Component Type to INT breaks everything
    public static final VertexFormatElement MATERIAL = VertexFormatElement.register(
            7,
            0,
            VertexFormatElement.ComponentType.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );
    public static final VertexFormatElement ENTITY_POSITION = VertexFormatElement.register(
            8,
            0,
            VertexFormatElement.ComponentType.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            4);


    /**
     * CUSTOM VERTEX FORMATS
     */
    public static final VertexFormat POSITION_POSITION = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("Entity_Position", ENTITY_POSITION)
            .build();

    public static final VertexFormat POSITION_TEXTURE_NORMAL = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV_0)
            .add("Normal", VertexFormatElement.NORMAL)
            .build();

    public static final VertexFormat BLOCK = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV0", VertexFormatElement.UV_0)
            .add("UV1", VertexFormatElement.UV_1)
            .add("UV2", VertexFormatElement.UV_2)
            .add("Normal", VertexFormatElement.NORMAL)
            .add("Material", MATERIAL)
            .skip(1)
            .build();


    /**
     * CUSTOM RENDER LAYERS
     */

    private static final RenderPhase.ShaderProgram METEOR_SHADER = VeilRenderBridge.shaderState(DestroyingMinecraft.idOf("meteor/meteor"));
    private static final RenderPhase.ShaderProgram ENTITY_BLOOM_SHADER = VeilRenderBridge.shaderState(DestroyingMinecraft.idOf("star_piercer/star_piercer"));
//    public static final RenderPhase.ShaderProgram ENTITY_SOLID_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntitySolidProgram);
//
//    public static ShaderProgram getRenderTypeEntitySolidProgram() {
//        return ENTITY_BLOOM_SHADER;
//    }

    public static final RenderLayer METEOR = RenderLayer.of(
            "meteor",
            POSITION_POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(METEOR_SHADER)
                    .build(false)
    );

    public static final BiFunction<Identifier, Identifier, RenderLayer> ENTITY_BLOOM = Util.memoize (
            (BiFunction<Identifier, Identifier, RenderLayer>)((texture, bloomTexture) -> {
                return RenderLayer.of(
                        "entity_bloom",
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                        VertexFormat.DrawMode.QUADS,
                        1536,
                        true,
                        false,
                        RenderLayer.MultiPhaseParameters.builder()
                                .texture(RenderPhase.Textures.create()
                                        .add(texture, false, false)
                                        .add(bloomTexture, false, false)
                                        .add(bloomTexture, false, false)
                                        .add(bloomTexture, false, false)
                                        .build())
                                .transparency(NO_TRANSPARENCY)
                                .lightmap(ENABLE_LIGHTMAP)
                                .overlay(ENABLE_OVERLAY_COLOR)
                                .program(ENTITY_BLOOM_SHADER)
                                .build(true)
                );
            })
    );

}
