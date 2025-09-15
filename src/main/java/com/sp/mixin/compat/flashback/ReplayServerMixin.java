package com.sp.mixin.compat.flashback;

import com.moulberry.flashback.playback.ReplayServer;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReplayServer.class)
public class ReplayServerMixin {

    @Inject(method = "handleActions", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/playback/ReplayServer;clearDataForPlayingSnapshot()V", ordinal = 0))
    private void test(CallbackInfo ci) {
        //Apparently there's no server world to update???

        //Update Client animation
        World clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld != null) {
            WorldDestructionEventsComponent component2 = InitializeComponents.EVENTS.get(clientWorld);
            if (component2.getCurrentDestructionEvent() != null) {
                component2.getCurrentDestructionEvent().resetAnimationToCurrentTime(clientWorld);
            }
        }

    }

}
