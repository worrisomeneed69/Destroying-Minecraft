package com.sp.mixin;

import com.sp.DestroyingMinecraftClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Keyboard;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Enables global debug for everything that may use it.
 * Taken from the found footage mod
 */
@Mixin(Keyboard.class)
public abstract class EnableDebugMixin {

    @Shadow protected abstract void debugLog(Text text);

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void onHandleDebugKeys(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (keyCode == 70) { // F key
                DestroyingMinecraftClient.shouldRenderDebug = !DestroyingMinecraftClient.shouldRenderDebug;
                this.debugLog(Text.literal("Destroying Minecraft Debug Mode: " + (DestroyingMinecraftClient.shouldRenderDebug ? "Enabled" : "Disabled")));
                cir.setReturnValue(true);
            }
        }
    }

}
