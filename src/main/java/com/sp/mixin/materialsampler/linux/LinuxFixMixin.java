package com.sp.mixin.materialsampler.linux;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import foundry.veil.api.client.render.framebuffer.AdvancedFboTextureAttachment;
import foundry.veil.impl.client.render.framebuffer.DSAAdvancedFboImpl;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBClearTexture.glClearTexImage;

@Mixin(value = DSAAdvancedFboImpl.class, remap = false)
public class LinuxFixMixin {

    @WrapOperation(method = "clear", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBClearTexture;glClearTexImage(IIIILjava/nio/FloatBuffer;)V", ordinal = 0))
    private void redirectMethod(int textureId, int level, int format, int type, FloatBuffer data, Operation<Void> original, @Local AdvancedFboTextureAttachment texture) {
        while (GL11.glGetError() != GL11.GL_NO_ERROR) {
            System.out.println("CLEARING");
        }

        if (texture.getFormat() == GL30C.GL_R8UI) {
            original.call(textureId, level, GL30C.GL_RED_INTEGER, GL11C.GL_INT, data);
//            int error = GL11.glGetError();
//            if (error != GL11.GL_NO_ERROR) {
//                System.err.println("ERROR 1");
//            }
            return;
        }

        if (texture.getFormat() == 0) {
            return;
        }

        original.call(textureId, level, format, type, data);
//        int error = GL11.glGetError();
//        if (error != GL11.GL_NO_ERROR) {
//            System.out.println(texture.getName() + ": " + texture.getFormat());
//            System.err.println("ERROR 2");
//        }
    }

//    @ModifyArgs(method = "clear", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBClearTexture;glClearTexImage(IIIILjava/nio/FloatBuffer;)V", ordinal = 0))
//    private void fix(Args args, @Local AdvancedFboTextureAttachment texture) {
//        if (texture.getFormat() == 0) {
//            return;
//        }
//
//        if (texture.getFormat() == GL30C.GL_R8I) {
//            System.out.println(texture.getName() + ": " + texture.getFormat());
//            args.set(2, GL30C.GL_RED_INTEGER);
//            args.set(3, GL11C.GL_INT);
//        }
//    }

//    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBClearTexture;glClearTexImage(IIIILjava/nio/FloatBuffer;)V", ordinal = 0, shift = At.Shift.BEFORE))
//    private void isThereAnError(float red, float green, float blue, float alpha, float depth, int clearMask, int[] buffers, CallbackInfo ci) {
//
//        while (GL11.glGetError() != GL11.GL_NO_ERROR) {
//            System.out.println("CLEARING");
//        }
//    }
//
//    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBClearTexture;glClearTexImage(IIIILjava/nio/FloatBuffer;)V", ordinal = 0, shift = At.Shift.AFTER))
//    private void isThereAnError2(float red, float green, float blue, float alpha, float depth, int clearMask, int[] buffers, CallbackInfo ci) {
//
//        int error = GL11.glGetError();
//        if (error != GL11.GL_NO_ERROR) {
//            System.err.println("OpenGL Error: at " + new Exception().getStackTrace()[0]);
//            // The stack trace can help pinpoint the exact line where the error check occurred.
//        }
//    }

}
