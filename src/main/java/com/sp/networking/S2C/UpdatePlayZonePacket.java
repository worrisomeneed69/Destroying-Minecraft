package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Box;

public class UpdatePlayZonePacket {

    public static void receive(UpdatePlayZonePayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            if (payload.remove) {
                PlayZoneManager.removePlayZone(payload.playZoneID);
            } else {
                PlayZone playZone = new PlayZone(new Box(payload.minX, payload.minY, payload.minZ, payload.maxX, payload.maxY, payload.maxZ), payload.playZoneID);
                PlayZoneManager.addPlayZone(context.client().world, playZone);
            }
        });
    }


    public record UpdatePlayZonePayload(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, int playZoneID, boolean remove) implements CustomPayload {
        public static final Id<UpdatePlayZonePayload> ID = new Id<>(DestroyingMinecraft.idOf("upd_pz"));

        public static final PacketCodec<RegistryByteBuf, UpdatePlayZonePayload> CODEC = new PacketCodec<>() {
            @Override
            public UpdatePlayZonePayload decode(RegistryByteBuf buf) {
                double minX = PacketCodecs.DOUBLE.decode(buf);
                double maxX = PacketCodecs.DOUBLE.decode(buf);
                double minY = PacketCodecs.DOUBLE.decode(buf);
                double maxY = PacketCodecs.DOUBLE.decode(buf);
                double minZ = PacketCodecs.DOUBLE.decode(buf);
                double maxZ = PacketCodecs.DOUBLE.decode(buf);
                int playZoneID = PacketCodecs.INTEGER.decode(buf);
                boolean remove = PacketCodecs.BOOL.decode(buf);
                return new UpdatePlayZonePayload(minX, maxX, minY, maxY, minZ, maxZ, playZoneID, remove);
            }

            @Override
            public void encode(RegistryByteBuf buf, UpdatePlayZonePayload value) {
                PacketCodecs.DOUBLE.encode(buf, value.minX);
                PacketCodecs.DOUBLE.encode(buf, value.maxX);
                PacketCodecs.DOUBLE.encode(buf, value.minY);
                PacketCodecs.DOUBLE.encode(buf, value.maxY);
                PacketCodecs.DOUBLE.encode(buf, value.minZ);
                PacketCodecs.DOUBLE.encode(buf, value.maxZ);
                PacketCodecs.INTEGER.encode(buf, value.playZoneID);
                PacketCodecs.BOOL.encode(buf, value.remove);
            }
        };


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
