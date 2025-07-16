package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.PointCameraShake;
import com.sp.render.gui.DestructionTitleRenderCallback;
import com.sp.util.MathUtil;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class PointSBEPacket {

    public static void receive(SBEPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            PointCameraShake cameraShake = new PointCameraShake(MathUtil.toVec3d(payload.position()), (float) payload.radius(), 40, Easing.LINEAR);
            CameraShakeManager.addCameraShake(cameraShake);
        });
    }


    public record SBEPayload(Vector3f position, int radius) implements CustomPayload {
        public static final Id<SBEPayload> ID = new Id<>(DestroyingMinecraft.idOf("sbe"));

        public static final PacketCodec<RegistryByteBuf, SBEPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.VECTOR3F, SBEPayload::position,
                PacketCodecs.INTEGER, SBEPayload::radius,
                SBEPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
