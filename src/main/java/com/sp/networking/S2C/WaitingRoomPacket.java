package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;

public class WaitingRoomPacket {

    public static void receive(WaitingRoomPacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            PlayerComponent component = InitializeComponents.PLAYERS.get(context.player());

            component.setInWaitingRoom(payload.setInWaitingRoom);
            MinecraftClient.getInstance().options.hudHidden = payload.setInWaitingRoom;
        });
    }


    public record WaitingRoomPacketPayload(boolean setInWaitingRoom) implements CustomPayload {
        public static final Id<WaitingRoomPacketPayload> ID = new Id<>(DestroyingMinecraft.idOf("wtingrm"));

        public static final PacketCodec<RegistryByteBuf, WaitingRoomPacketPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.BOOL, WaitingRoomPacketPayload::setInWaitingRoom,
                WaitingRoomPacketPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
