package com.sp.block.entity.custom;

import com.sp.block.entity.ModBlockEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.render.gui.PhysicsDoorBlockScreen;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.DoorOpeningLoopSoundInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhysicsDoorBlockEntity extends BlockEntity {
    private BlockPos corner1 = BlockPos.ORIGIN;
    private BlockPos corner2 = BlockPos.ORIGIN;
    private Direction movementDirection = Direction.UP;
    private int numOfBlocks = 0;
    private int speed = 1;
    private boolean showSelection;
    private boolean useSound = true;
    private boolean settingSelection;
    private boolean open;
    private boolean doorMoving;
    private Vec3d startingPos;
    private BlockPhysicsEntity currentDoor;
    private DoorOpeningLoopSoundInstance doorOpeningSoundInstance;

    public PhysicsDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PHYSICS_DOOR_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("corner1", NbtHelper.fromBlockPos(this.corner1));
        nbt.put("corner2", NbtHelper.fromBlockPos(this.corner2));
        nbt.putInt("direction", this.movementDirection.getId());
        nbt.putInt("numOfBlocks", this.numOfBlocks);
        nbt.putInt("speed", this.speed);
        nbt.putBoolean("showSelection", this.showSelection);
        nbt.putBoolean("useSound", this.useSound);
        nbt.putBoolean("doorMoving", this.doorMoving);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        this.corner1 = NbtHelper.toBlockPos(nbt, "corner1").orElse(null);
        this.corner2 = NbtHelper.toBlockPos(nbt, "corner2").orElse(null);
        this.movementDirection = Direction.byId(nbt.getInt("direction"));
        this.numOfBlocks = nbt.getInt("numOfBlocks");
        this.speed = nbt.getInt("speed");
        this.showSelection = nbt.getBoolean("showSelection");
        this.useSound = nbt.getBoolean("useSound");
        this.doorMoving = nbt.getBoolean("doorMoving");
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    public boolean openScreen(PlayerEntity player) {
        if (!player.isCreativeLevelTwoOp()) {
            return false;
        } else {
            if (player.getEntityWorld().isClient) {
                MinecraftClient.getInstance().setScreen(new PhysicsDoorBlockScreen(this));
            }

            return true;
        }
    }

    public void moveDoor(World world) {
        if(numOfBlocks <= 0) return;

        BlockPos corner1 = this.corner1;
        BlockPos corner2 = this.corner2;
        this.currentDoor = BlockPhysicsEntity.ofBlocks(world, corner1, corner2);
        this.startingPos = this.currentDoor.getPos();
        Vec3d velocity = new Vec3d(this.movementDirection.getOffsetX(), this.movementDirection.getOffsetY(), this.movementDirection.getOffsetZ()).multiply((double) this.speed / 400);
        this.currentDoor.setVelocity(velocity);
        this.currentDoor.velocityDirty = true;
        this.currentDoor.velocityModified = true;
        this.doorMoving = true;
        this.markDirty();
        world.updateListeners(this.getPos(), world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), Block.NOTIFY_ALL);
        if(useSound) world.playSound(null, this.currentDoor.getBlockPos(), ModSounds.DOOR_OPEN, SoundCategory.AMBIENT, 10, 1);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient && doorMoving && this.currentDoor != null) {
            Vec3d offset = new Vec3d(this.movementDirection.getOffsetX(), this.movementDirection.getOffsetY(), this.movementDirection.getOffsetZ());
            if (this.currentDoor.getPos().add(offset.subtract(offset).subtract(offset).multiply(numOfBlocks)).distanceTo(this.startingPos) <= 0.02) {
                this.currentDoor.setDown();
                this.currentDoor.markForDiscard();
                this.doorMoving = false;
                this.open = !this.open;

                if(useSound) world.playSound(null, this.currentDoor.getBlockPos(), ModSounds.DOOR_CLOSE, SoundCategory.AMBIENT, 10, 1);
                this.currentDoor = null;

                this.corner1 = corner1.offset(this.movementDirection, numOfBlocks);
                this.corner2 = corner2.offset(this.movementDirection, numOfBlocks);
                this.movementDirection = this.movementDirection.getOpposite();
                this.markDirty();
                world.updateListeners(this.getPos(), world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), Block.NOTIFY_ALL);
            }
        }

        if (world.isClient && this.shouldPlaySound()) {
            if (doorMoving && doorOpeningSoundInstance == null) {
                doorOpeningSoundInstance = new DoorOpeningLoopSoundInstance(this.getPos().toCenterPos());
                MinecraftClient.getInstance().getSoundManager().play(doorOpeningSoundInstance);
            } else if(!doorMoving) {
                if (doorOpeningSoundInstance != null) {
                    doorOpeningSoundInstance.startFadeOut();
                    doorOpeningSoundInstance = null;
                }
            }
        }
    }

    public BlockPos getCorner1() {
        return this.corner1;
    }
    public void setCorner1(BlockPos corner1) {
        this.corner1 = corner1;
    }

    public BlockPos getCorner2() {
        return this.corner2;
    }
    public void setCorner2(BlockPos pos2) {
        this.corner2 = pos2;
    }

    public Direction getMovementDirection() {
        return this.movementDirection;
    }
    public void setMovementDirection(Direction movementDirection) {
        this.movementDirection = movementDirection;
    }

    public int getNumOfBlocks() {
        return this.numOfBlocks;
    }
    public void setNumOfBlocks(int numOfBlocks) {
        this.numOfBlocks = numOfBlocks;
    }

    public int getSpeed() {
        return this.speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isSettingSelection() {
        return this.settingSelection;
    }
    public void setSettingSelection(boolean settingSelection) {
        this.settingSelection = settingSelection;
    }

    public boolean shouldShowSelection() {
        return this.showSelection;
    }
    public void setShowSelection(boolean showSelection) {
        this.showSelection = showSelection;
    }

    public boolean shouldPlaySound() {
        return this.useSound;
    }
    public void setPlaySound(boolean useSound) {
        this.useSound = useSound;
    }

    public boolean isDoorMoving() {
        return this.doorMoving;
    }
}
