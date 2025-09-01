package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.networking.CustomPayloads;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Box;

public class UpdatePlayZonePacket {

    public static void receive(CustomPayloads.UpdatePlayZonePayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            if (payload.remove()) {
                PlayZoneManager.removePlayZone(payload.playZoneID());
            } else {
                PlayZone playZone = new PlayZone(new Box(payload.minX(), payload.minY(), payload.minZ(), payload.maxX(), payload.maxY(), payload.maxZ()), payload.playZoneID());
                PlayZoneManager.addPlayZone(context.client().world, playZone);
            }
        });
    }
}
