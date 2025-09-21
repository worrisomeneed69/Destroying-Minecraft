package com.sp.networking.S2C;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.networking.CustomPayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class WaitingRoomPacket {

    public static void receive(CustomPayloads.WaitingRoomPacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            if (payload.setInWaitingRoom()) {
                context.client().getSoundManager().close();
            } else {
                context.client().getSoundManager().reloadSounds();
            }

            MinecraftClient.getInstance().options.hudHidden = payload.setInWaitingRoom();

            //Reset all events
            for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                event.setActive(false, -1);
                event.resetEvent();
            }
        });
    }
}
