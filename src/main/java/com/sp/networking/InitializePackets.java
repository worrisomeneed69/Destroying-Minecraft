package com.sp.networking;

import com.sp.DestroyingMinecraft;
import com.sp.networking.S2C.InvokeDestructionPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public class InitializePackets {


    public static void registerServerNetworking(){
        PayloadTypeRegistry.playS2C().register(DestructionPayload.ID, DestructionPayload.CODEC);
    }

    public static void registerClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(DestructionPayload.ID, InvokeDestructionPacket::receive);
    }


    public record DestructionPayload(int start, int type) implements CustomPayload{
        public static final CustomPayload.Id<DestructionPayload> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("dest"));
        public static final PacketCodec<RegistryByteBuf, DestructionPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, DestructionPayload::start,
                PacketCodecs.INTEGER, DestructionPayload::type,
                DestructionPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
