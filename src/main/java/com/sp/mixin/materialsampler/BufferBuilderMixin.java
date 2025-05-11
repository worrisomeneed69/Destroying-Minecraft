package com.sp.mixin.materialsampler;

import com.sp.mixininterfaces.BlockMaterial;
import com.sp.render.materialsampler.CustomVertexFormats;
import net.minecraft.block.Block;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This Mixin adds additional vertex data to blocks when rendering:<br>
 * MaterialID for every block
 */
@Mixin(value = BufferBuilder.class, priority = 999)
public abstract class BufferBuilderMixin implements BlockMaterial {

    @Shadow protected abstract long beginElement(VertexFormatElement element);

    @Unique boolean isRenderingBlock;
    @Unique Block currentBlock;
    @Unique VertexFormat currentFormat;

    @Override
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/VertexFormatElement;POSITION:Lnet/minecraft/client/render/VertexFormatElement;", ordinal = 1), argsOnly = true)
    private VertexFormat setFormat(VertexFormat format) {
        this.isRenderingBlock = false;
        this.currentFormat = format;

        //Rendering a normal block. Redirect it to include the Custom Material
        if (format == net.minecraft.client.render.VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL || format == CustomVertexFormats.BLOCK) {
            System.out.println("W1");
            this.isRenderingBlock = true;
            this.currentFormat = CustomVertexFormats.BLOCK;
            return CustomVertexFormats.BLOCK;
        }

        return format;
    }


    @Inject(method = "vertex(FFF)Lnet/minecraft/client/render/VertexConsumer;", at = @At("RETURN"))
    private void putBlockID(float x, float y, float z, CallbackInfoReturnable<VertexConsumer> cir) {

        if (this.isRenderingBlock) {
//            System.out.println("W1");
            //Normal Block
            if(currentFormat == CustomVertexFormats.BLOCK) {
//                System.out.println("W2");
                long midBlockOffset = this.beginElement(CustomVertexFormats.MATERIAL);
                MemoryUtil.memPutInt(midBlockOffset, 1);
            }
        }
    }

//    @Inject(method = "endVertex", at = @At("HEAD"))
//    private void beforeNext(CallbackInfo ci){
//
//    }

}