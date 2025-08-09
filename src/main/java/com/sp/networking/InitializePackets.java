package com.sp.networking;

import com.sp.networking.C2S.UpdateLimboSquareBlockPacket;
import com.sp.networking.C2S.UpdatePhysicsDoorPacket;
import com.sp.networking.S2C.InvokeDestructionPacket;
import com.sp.networking.S2C.BraamPacket;
import com.sp.networking.S2C.PointSBEPacket;
import com.sp.networking.S2C.UpdatePlayZonePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class InitializePackets {


    public static void registerServerNetworking() {
        //Send to server
        PayloadTypeRegistry.playC2S().register(UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.ID, UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.CODEC);
        PayloadTypeRegistry.playC2S().register(UpdateLimboSquareBlockPacket.UpdateLimboSquareBlockPayload.ID, UpdateLimboSquareBlockPacket.UpdateLimboSquareBlockPayload.CODEC);

        //Receive from client
        ServerPlayNetworking.registerGlobalReceiver(UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.ID, UpdatePhysicsDoorPacket::recieve);
        ServerPlayNetworking.registerGlobalReceiver(UpdateLimboSquareBlockPacket.UpdateLimboSquareBlockPayload.ID, UpdateLimboSquareBlockPacket::receive);


        //Send to client
        PayloadTypeRegistry.playS2C().register(InvokeDestructionPacket.DestructionPayload.ID, InvokeDestructionPacket.DestructionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PointSBEPacket.SBEPayload.ID, PointSBEPacket.SBEPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BraamPacket.BraamPayload.ID, BraamPacket.BraamPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdatePlayZonePacket.UpdatePlayZonePayload.ID, UpdatePlayZonePacket.UpdatePlayZonePayload.CODEC);
    }


    public static void registerClientNetworking() {
        //Receive from server
        ClientPlayNetworking.registerGlobalReceiver(InvokeDestructionPacket.DestructionPayload.ID, InvokeDestructionPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PointSBEPacket.SBEPayload.ID, PointSBEPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BraamPacket.BraamPayload.ID, BraamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(UpdatePlayZonePacket.UpdatePlayZonePayload.ID, UpdatePlayZonePacket::receive);
    }

}
