package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.networking.CustomPayloads;
import com.sp.render.ShaderType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ShaderChangePacket {

    public static void receive(CustomPayloads.ShaderChangePacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            DestroyingMinecraftConfig.shaderType = ShaderType.getFromString(payload.shader());
            DestroyingMinecraftConfig.write(DestroyingMinecraft.MOD_ID);
        });
    }

}
