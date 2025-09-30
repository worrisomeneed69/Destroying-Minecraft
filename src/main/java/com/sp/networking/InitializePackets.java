package com.sp.networking;

import com.sp.networking.C2S.UpdatePhysicsDoorPacket;
import com.sp.networking.S2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class InitializePackets {


    public static void registerServerNetworking() {
        //Send to server
        PayloadTypeRegistry.playC2S().register(CustomPayloads.UpdatePhysicsDoorBlock.ID, CustomPayloads.UpdatePhysicsDoorBlock.CODEC);

        //Receive from client
        ServerPlayNetworking.registerGlobalReceiver(CustomPayloads.UpdatePhysicsDoorBlock.ID, UpdatePhysicsDoorPacket::recieve);


        //Send to client
        PayloadTypeRegistry.playS2C().register(CustomPayloads.DestructionPayload.ID, CustomPayloads.DestructionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.SBEPayload.ID, CustomPayloads.SBEPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.BraamPayload.ID, CustomPayloads.BraamPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.UpdatePlayZonePayload.ID, CustomPayloads.UpdatePlayZonePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.WaitingRoomPacketPayload.ID, CustomPayloads.WaitingRoomPacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.ShaderChangePacketPayload.ID, CustomPayloads.ShaderChangePacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CustomPayloads.LavaSpewPacketPayload.ID, CustomPayloads.LavaSpewPacketPayload.CODEC);
    }


    public static void registerClientNetworking() {
        //Receive from server
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.DestructionPayload.ID, InvokeDestructionPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.SBEPayload.ID, PointSBEPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.BraamPayload.ID, BraamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.UpdatePlayZonePayload.ID, UpdatePlayZonePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.WaitingRoomPacketPayload.ID, WaitingRoomPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.ShaderChangePacketPayload.ID, ShaderChangePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CustomPayloads.LavaSpewPacketPayload.ID, LavaSpewPacket::receive);
    }

}
