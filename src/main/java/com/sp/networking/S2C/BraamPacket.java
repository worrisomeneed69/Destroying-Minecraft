package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;

public class BraamPacket {

    public static void receive(BraamPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            context.client().getSoundManager().play(PositionedSoundInstance.ambient(payload.soundEvent, 1.0f, 1.0f));
            CameraShakeInstance cameraShake = new CameraShakeInstance(1.0f, 0, 60, Easing.EASE_IN_EXPO);
            CameraShakeManager.addCameraShake(cameraShake);
        });
    }


    public record BraamPayload(SoundEvent soundEvent) implements CustomPayload {
        public static final Id<BraamPayload> ID = new Id<>(DestroyingMinecraft.idOf("asp"));

        public static final PacketCodec<RegistryByteBuf, BraamPayload> CODEC = PacketCodec.tuple(
                SoundEvent.PACKET_CODEC, BraamPayload::soundEvent,
                BraamPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
