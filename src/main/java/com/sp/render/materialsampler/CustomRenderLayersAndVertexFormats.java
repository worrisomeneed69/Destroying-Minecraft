package com.sp.render.materialsampler;

import com.sp.DestroyingMinecraft;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.Identifier;

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

}
