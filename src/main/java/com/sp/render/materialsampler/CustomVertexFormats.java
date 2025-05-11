package com.sp.render.materialsampler;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

public class CustomVertexFormats {


    //For some reason setting the Component Type to INT breaks everything
    public static final VertexFormatElement MATERIAL = VertexFormatElement.register(
            6,
            0,
            VertexFormatElement.ComponentType.FLOAT,
            VertexFormatElement.Usage.GENERIC,
            1
    );

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

}
