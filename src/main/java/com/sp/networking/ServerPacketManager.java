package com.sp.networking;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.render.ShaderType;
import com.sp.world.playzone.PlayZone;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ServerPacketManager {

    public static void sendPointSBEPacket(PlayerEntity player, Vec3d position, int radius) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.SBEPayload(position.toVector3f(), radius));
    }

    public static void sendBraamPacket(PlayerEntity player, SoundEvent soundEvent) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.BraamPayload(soundEvent));
    }

    public static void sendUpdatePlayZonePacket(PlayerEntity player, PlayZone playZone, boolean remove) {
        Box playZoneBounds = playZone.getBoundingBox();
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.UpdatePlayZonePayload(
                playZoneBounds.minX,
                playZoneBounds.maxX,
                playZoneBounds.minY,
                playZoneBounds.maxY,
                playZoneBounds.minZ,
                playZoneBounds.maxZ,
                playZone.getId(),
                remove
        ));
    }

    public static void sendWaitingRoomPacket(PlayerEntity player, boolean setInWaitingRoom) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.WaitingRoomPacketPayload(setInWaitingRoom));
    }

    public static void sendShaderChangePacket(PlayerEntity player, ShaderType shaderType) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.ShaderChangePacketPayload(shaderType.asString()));
    }

    public static void sendLavaSpewPacket(PlayerEntity player, Vec3d pos) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new CustomPayloads.LavaSpewPacketPayload(pos.toVector3f()));
    }

}
