package com.sp.mixin.materialsampler;

import com.sp.render.materialsampler.CustomDynamicBuffers;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(DynamicBufferType.class)
@Unique
public abstract class DynamicBufferTypeMixin {

    @Shadow
    @Final
    @Mutable
    private static DynamicBufferType[] $VALUES;

    private static final DynamicBufferType MATERIAL = CustomDynamicBuffers.MATERIAL_BUFFER = addVariant("Material",GlslTypeSpecifier.BuiltinType.IVEC4, FramebufferAttachmentDefinition.Format.R8UI);
    private static final DynamicBufferType BLOOM = CustomDynamicBuffers.BLOOM_BUFFER = addVariant("Bloom",GlslTypeSpecifier.BuiltinType.VEC3, FramebufferAttachmentDefinition.Format.R11F_G11F_B10F);

    @Shadow
    @Final
    @Mutable
    public static DynamicBufferType[] BUFFERS = {DynamicBufferType.ALBEDO, DynamicBufferType.NORMAL, DynamicBufferType.LIGHT_UV, DynamicBufferType.LIGHT_COLOR, DynamicBufferType.DEBUG, CustomDynamicBuffers.MATERIAL_BUFFER, CustomDynamicBuffers.BLOOM_BUFFER};

    @Shadow
    @Final
    @Mutable
    private static int MASK = (1 << BUFFERS.length) - 1;


    @Invoker("<init>")
    public static DynamicBufferType invokeInit(String par1, int par2, String par3, GlslTypeSpecifier.BuiltinType par4, FramebufferAttachmentDefinition.Format par5) {
        throw new AssertionError();
    }

    private static DynamicBufferType addVariant(String sourceName, GlslTypeSpecifier.BuiltinType type, FramebufferAttachmentDefinition.Format format) {
        ArrayList<DynamicBufferType> variants = new ArrayList<DynamicBufferType>(Arrays.asList(DynamicBufferTypeMixin.$VALUES));
        DynamicBufferType bufferType = invokeInit(sourceName, variants.get(variants.size() - 1).ordinal() + 1, sourceName, type, format);
        variants.add(bufferType);
        DynamicBufferType[] newList = variants.toArray(DynamicBufferTypeMixin.$VALUES);
        $VALUES = newList;
        return bufferType;
    }

}
