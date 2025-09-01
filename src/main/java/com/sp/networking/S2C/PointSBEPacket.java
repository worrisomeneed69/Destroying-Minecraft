package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.networking.CustomPayloads;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.PointCameraShake;
import com.sp.util.MathUtil;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

public class PointSBEPacket {

    public static void receive(CustomPayloads.SBEPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            PointCameraShake cameraShake = new PointCameraShake(MathUtil.toVec3d(payload.position()), (float) payload.radius(), 40, Easing.LINEAR);
            CameraShakeManager.addCameraShake(cameraShake);
        });
    }

}
