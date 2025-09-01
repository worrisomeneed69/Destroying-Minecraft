package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.networking.CustomPayloads;
import com.sp.render.ShaderType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public class ShaderChangePacket {

    public static void receive(CustomPayloads.ShaderChangePacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            DestroyingMinecraftConfig.shaderType = ShaderType.getFromString(payload.shader());
        });
    }

}
