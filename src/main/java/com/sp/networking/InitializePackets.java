package com.sp.networking;

import com.sp.DestroyingMinecraft;
import com.sp.networking.S2C.InvokeSupernovaPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public class InitializePackets {


    public static void registerServerNetworking(){
        PayloadTypeRegistry.playS2C().register(SupernovaPayload.ID, SupernovaPayload.CODEC);
    }

    public static void registerClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(SupernovaPayload.ID, InvokeSupernovaPacket::receive);
    }


    public record SupernovaPayload(int start) implements CustomPayload{
        public static final CustomPayload.Id<SupernovaPayload> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("supno"));
        public static final PacketCodec<RegistryByteBuf, SupernovaPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SupernovaPayload::start, SupernovaPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
