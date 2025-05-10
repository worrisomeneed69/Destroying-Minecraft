package com.sp.mixin.materialsampler;

import com.sp.DestroyingMinecraftClient;
import com.sp.render.CustomDynamicBuffers;
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

    private static final DynamicBufferType MATERIAL = CustomDynamicBuffers.MATERIAL_BUFFER = addVariant("Material",GlslTypeSpecifier.BuiltinType.VEC4, FramebufferAttachmentDefinition.Format.R8UI);



    @Invoker("<init>")
    public static DynamicBufferType invokeInit(String par1, int par2, String par3, GlslTypeSpecifier.BuiltinType par4, FramebufferAttachmentDefinition.Format par5) {
        throw new AssertionError();
    }

    private static DynamicBufferType addVariant(String sourceName, GlslTypeSpecifier.BuiltinType type, FramebufferAttachmentDefinition.Format format) {
        ArrayList<DynamicBufferType> variants = new ArrayList<DynamicBufferType>(Arrays.asList(DynamicBufferTypeMixin.$VALUES));
        DynamicBufferType bufferType = invokeInit(sourceName, variants.get(variants.size() - 1).ordinal() + 1, sourceName, type, format);
        variants.add(bufferType);
        DynamicBufferTypeMixin.$VALUES = variants.toArray(new DynamicBufferType[0]);
        return bufferType;
    }

}
