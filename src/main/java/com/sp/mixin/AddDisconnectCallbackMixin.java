package com.sp.mixin;

import com.sp.networking.callbacks.ClientConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class AddDisconnectCallbackMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo ci){
        if(!(screen instanceof ProgressScreen)) {
            ClientConnectionEvents.DISCONNECT.invoker().onLoginDisconnect((MinecraftClient) (Object) this);
        }
    }

}
