package com.sp.networking.S2C;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.networking.CustomPayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class WaitingRoomPacket {

    public static void receive(CustomPayloads.WaitingRoomPacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            PlayerComponent component = InitializeComponents.PLAYERS.get(context.player());
            context.client().getSoundManager().stopAll();
            component.setInWaitingRoom(payload.setInWaitingRoom());
            MinecraftClient.getInstance().options.hudHidden = payload.setInWaitingRoom();
        });
    }
}
