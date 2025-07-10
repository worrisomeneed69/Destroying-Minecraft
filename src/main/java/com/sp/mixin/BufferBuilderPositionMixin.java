package com.sp.mixin;

import com.sp.mixininterfaces.BufferBuilderPosition;
import com.sp.render.CustomRenderLayersAndVertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormatElement;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderPositionMixin implements BufferBuilderPosition {

    @Shadow protected abstract long beginElement(VertexFormatElement element);

    @Shadow
    private static byte floatToByte(float f) {
        return 0;
    }

    @Override
    public void setPosition(float x, float y, float z, float w) {
        long l = this.beginElement(CustomRenderLayersAndVertexFormats.ENTITY_POSITION);
        if (l != -1L) {
            MemoryUtil.memPutFloat(l, x);
            MemoryUtil.memPutFloat(l + 4L, y);
            MemoryUtil.memPutFloat(l + 8L, z);
            MemoryUtil.memPutFloat(l + 12L, w);
        }
    }

}
