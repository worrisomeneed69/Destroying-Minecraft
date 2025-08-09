package com.sp.networking.C2S;

import com.sp.DestroyingMinecraft;
import com.sp.block.entity.custom.LimboSquareBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

public class UpdateLimboSquareBlockPacket {
    public static void receive(UpdateLimboSquareBlockPayload payload, ServerPlayNetworking.Context context) {
        BlockEntity blockEntity = context.player().getWorld().getBlockEntity(payload.blockEntityPos);
        BlockState blockState = context.player().getWorld().getBlockState(payload.blockEntityPos);

        if (blockEntity instanceof LimboSquareBlockEntity limboSquareBlockEntity) {
            limboSquareBlockEntity.setColor(payload.color);
            limboSquareBlockEntity.setSize(payload.size);
            limboSquareBlockEntity.setHeight(payload.height);
            limboSquareBlockEntity.markDirty();
            context.player().getWorld().updateListeners(payload.blockEntityPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }



    public record UpdateLimboSquareBlockPayload(BlockPos blockEntityPos, Vector3f color, float size, float height) implements CustomPayload {
        public static final Id<UpdateLimboSquareBlockPayload> ID = new Id<>(DestroyingMinecraft.idOf("updatelimboblk"));

        public static final PacketCodec<RegistryByteBuf, UpdateLimboSquareBlockPayload> CODEC = PacketCodec.tuple(
                BlockPos.PACKET_CODEC, UpdateLimboSquareBlockPayload::blockEntityPos,
                PacketCodecs.VECTOR3F, UpdateLimboSquareBlockPayload::color,
                PacketCodecs.FLOAT, UpdateLimboSquareBlockPayload::size,
                PacketCodecs.FLOAT, UpdateLimboSquareBlockPayload::height,
                UpdateLimboSquareBlockPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
