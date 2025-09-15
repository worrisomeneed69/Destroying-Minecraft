package com.sp.networking;

import com.sp.DestroyingMinecraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public class CustomPayloads {

    ///////////////////////////// C2S /////////////////////////////
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

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

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



    ///////////////////////////// S2C /////////////////////////////
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

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public record DestructionPayload(int type, long startTime) implements CustomPayload {
        public static final CustomPayload.Id<DestructionPayload> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("dest"));

        public static final PacketCodec<RegistryByteBuf, DestructionPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, DestructionPayload::type,
                PacketCodecs.VAR_LONG, DestructionPayload::startTime,
                DestructionPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public record ShaderChangePacketPayload(String shader) implements CustomPayload {
        public static final Id<ShaderChangePacketPayload> ID = new Id<>(DestroyingMinecraft.idOf("shdrchng"));

        public static final PacketCodec<RegistryByteBuf, ShaderChangePacketPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, ShaderChangePacketPayload::shader,
                ShaderChangePacketPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public record UpdatePlayZonePayload(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, int playZoneID, boolean remove) implements CustomPayload {
        public static final Id<UpdatePlayZonePayload> ID = new Id<>(DestroyingMinecraft.idOf("upd_pz"));

        public static final PacketCodec<RegistryByteBuf, UpdatePlayZonePayload> CODEC = new PacketCodec<>() {
            @Override
            public UpdatePlayZonePayload decode(RegistryByteBuf buf) {
                double minX = PacketCodecs.DOUBLE.decode(buf);
                double maxX = PacketCodecs.DOUBLE.decode(buf);
                double minY = PacketCodecs.DOUBLE.decode(buf);
                double maxY = PacketCodecs.DOUBLE.decode(buf);
                double minZ = PacketCodecs.DOUBLE.decode(buf);
                double maxZ = PacketCodecs.DOUBLE.decode(buf);
                int playZoneID = PacketCodecs.INTEGER.decode(buf);
                boolean remove = PacketCodecs.BOOL.decode(buf);
                return new UpdatePlayZonePayload(minX, maxX, minY, maxY, minZ, maxZ, playZoneID, remove);
            }

            @Override
            public void encode(RegistryByteBuf buf, UpdatePlayZonePayload value) {
                PacketCodecs.DOUBLE.encode(buf, value.minX);
                PacketCodecs.DOUBLE.encode(buf, value.maxX);
                PacketCodecs.DOUBLE.encode(buf, value.minY);
                PacketCodecs.DOUBLE.encode(buf, value.maxY);
                PacketCodecs.DOUBLE.encode(buf, value.minZ);
                PacketCodecs.DOUBLE.encode(buf, value.maxZ);
                PacketCodecs.INTEGER.encode(buf, value.playZoneID);
                PacketCodecs.BOOL.encode(buf, value.remove);
            }
        };


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public record LavaSpewPacketPayload(Vector3f position) implements CustomPayload {
        public static final Id<LavaSpewPacketPayload> ID = new Id<>(DestroyingMinecraft.idOf("lvaspw"));

        public static final PacketCodec<RegistryByteBuf, LavaSpewPacketPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.VECTOR3F, LavaSpewPacketPayload::position,
                LavaSpewPacketPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
