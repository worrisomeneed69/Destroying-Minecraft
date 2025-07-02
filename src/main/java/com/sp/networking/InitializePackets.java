package com.sp.networking;

import com.sp.networking.C2S.UpdatePhysicsDoorPacket;
import com.sp.networking.S2C.InvokeDestructionPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class InitializePackets {


    public static void registerServerNetworking() {
        //Send to server
        PayloadTypeRegistry.playC2S().register(UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.ID, UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.CODEC);

        //Receive from client
        ServerPlayNetworking.registerGlobalReceiver(UpdatePhysicsDoorPacket.UpdatePhysicsDoorBlock.ID, UpdatePhysicsDoorPacket::recieve);


        //Send to client
        PayloadTypeRegistry.playS2C().register(InvokeDestructionPacket.DestructionPayload.ID, InvokeDestructionPacket.DestructionPayload.CODEC);
    }


    public static void registerClientNetworking() {
        //Receive from server
        ClientPlayNetworking.registerGlobalReceiver(InvokeDestructionPacket.DestructionPayload.ID, InvokeDestructionPacket::receive);
    }

}
