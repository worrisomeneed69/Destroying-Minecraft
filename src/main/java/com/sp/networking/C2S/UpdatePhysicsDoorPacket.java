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
            physicsDoorBlockEntity.markDirty();
            context.player().getWorld().updateListeners(payload.blockEntityPos, blockState, blockState, Block.NOTIFY_ALL);
        }
    }



    public record UpdatePhysicsDoorBlock(BlockPos blockEntityPos, BlockPos corner1, BlockPos corner2, Direction direction, int numOfBlocks) implements CustomPayload {
        public static final CustomPayload.Id<UpdatePhysicsDoorBlock> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("updatephysdoorblk"));

        public static final PacketCodec<RegistryByteBuf, UpdatePhysicsDoorBlock> CODEC = PacketCodec.tuple(
                BlockPos.PACKET_CODEC, UpdatePhysicsDoorBlock::blockEntityPos,
                BlockPos.PACKET_CODEC, UpdatePhysicsDoorBlock::corner1,
                BlockPos.PACKET_CODEC, UpdatePhysicsDoorBlock::corner2,
                Direction.PACKET_CODEC, UpdatePhysicsDoorBlock::direction,
                PacketCodecs.INTEGER, UpdatePhysicsDoorBlock::numOfBlocks,
                UpdatePhysicsDoorBlock::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
