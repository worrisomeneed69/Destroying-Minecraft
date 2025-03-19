package com.sp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Inject(method = "registerDynamicTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;registerTexture(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/AbstractTexture;)V"))
    private void printTexture(String prefix, NativeImageBackedTexture texture, CallbackInfoReturnable<Identifier> cir, @Local Identifier identifier){
        System.out.println(identifier.toString());
    }

}
