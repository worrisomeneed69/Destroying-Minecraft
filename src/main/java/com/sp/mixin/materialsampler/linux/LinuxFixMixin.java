package com.sp.mixin.materialsampler.linux;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import foundry.veil.api.client.render.framebuffer.AdvancedFboTextureAttachment;
import foundry.veil.impl.client.render.framebuffer.DSAAdvancedFboImpl;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.nio.FloatBuffer;

@Mixin(value = DSAAdvancedFboImpl.class, remap = false)
public class LinuxFixMixin {

    @WrapOperation(method = "clear", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBClearTexture;glClearTexImage(IIIILjava/nio/FloatBuffer;)V", ordinal = 0))
    private void redirectMethod(int textureId, int level, int format, int type, FloatBuffer data, Operation<Void> original, @Local AdvancedFboTextureAttachment texture) {
        if (texture.getFormat() == GL30C.GL_R8UI) {
            original.call(textureId, level, GL30C.GL_RED_INTEGER, GL11C.GL_INT, data);
            return;
        }

        if (texture.getFormat() == 0) {
            return;
        }

        original.call(textureId, level, format, type, data);
    }

}
