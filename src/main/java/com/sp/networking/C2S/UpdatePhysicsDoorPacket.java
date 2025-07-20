package com.sp.networking.C2S;

import com.sp.DestroyingMinecraft;
import com.sp.block.entity.custom.PhysicsDoorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class UpdatePhysicsDoorPacket {
    public static void recieve(UpdatePhysicsDoorBlock payload, ServerPlayNetworking.Context context) {
        BlockEntity blockEntity = context.player().getWorld().getBlockEntity(payload.blockEntityPos);
        BlockState blockState = context.player().getWorld().getBlockState(payload.blockEntityPos);

        if (blockEntity instanceof PhysicsDoorBlockEntity physicsDoorBlockEntity) {
            physicsDoorBlockEntity.setCorner1(payload.corner1);
            physicsDoorBlockEntity.setCorner2(payload.corner2);
            physicsDoorBlockEntity.setMovementDirection(payload.direction);
            physicsDoorBlockEntity.setNumOfBlocks(payload.numOfBlocks);
            physicsDoorBlockEntity.setSpeed(payload.speed);
            physicsDoorBlockEntity.setShowSelection(payload.showSelection);
            physicsDoorBlockEntity.setPlaySound(payload.playSound);
            physicsDoorBlockEntity.markDirty();
            context.player().getWorld().updateListeners(payload.blockEntityPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }



    public record UpdatePhysicsDoorBlock(
            BlockPos blockEntityPos,
            BlockPos corner1,
            BlockPos corner2,
            Direction direction,
            int numOfBlocks,
            int speed,
            boolean showSelection,
            boolean playSound
    ) implements CustomPayload {
        public static final CustomPayload.Id<UpdatePhysicsDoorBlock> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("updatephysdoorblk"));

        public static final PacketCodec<RegistryByteBuf, UpdatePhysicsDoorBlock> CODEC = new PacketCodec<>() {
            @Override
            public UpdatePhysicsDoorBlock decode(RegistryByteBuf buf) {
                BlockPos entityPos = BlockPos.PACKET_CODEC.decode(buf);
                BlockPos corner1 = BlockPos.PACKET_CODEC.decode(buf);
                BlockPos corner2 = BlockPos.PACKET_CODEC.decode(buf);
                Direction direction = Direction.PACKET_CODEC.decode(buf);
                Integer numOfBlocks = PacketCodecs.INTEGER.decode(buf);
                Integer speed = PacketCodecs.INTEGER.decode(buf);
                Boolean showSelection = PacketCodecs.BOOL.decode(buf);
                Boolean playSound = PacketCodecs.BOOL.decode(buf);
                return new UpdatePhysicsDoorBlock(entityPos, corner1, corner2, direction, numOfBlocks,speed, showSelection, playSound);
            }

            @Override
            public void encode(RegistryByteBuf buf, UpdatePhysicsDoorBlock value) {
                BlockPos.PACKET_CODEC.encode(buf, value.blockEntityPos);
                BlockPos.PACKET_CODEC.encode(buf, value.corner1);
                BlockPos.PACKET_CODEC.encode(buf, value.corner2);
                Direction.PACKET_CODEC.encode(buf, value.direction);
                PacketCodecs.INTEGER.encode(buf, value.numOfBlocks);
                PacketCodecs.INTEGER.encode(buf, value.speed);
                PacketCodecs.BOOL.encode(buf, value.showSelection);
                PacketCodecs.BOOL.encode(buf, value.playSound);
            }
        };


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
