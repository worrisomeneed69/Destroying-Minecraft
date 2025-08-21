package com.sp.cca.custom.entity;

import com.sp.cca.InitializeComponents;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class PhysicsBlockComponent implements AutoSyncedComponent {
    private final BlockPhysicsEntity blockPhysicsEntity;
//    private List<BlockPhysicsEntity.BlockData> blocks = new ArrayList<>();
    private Quaternionf rotation = new Quaternionf();
    private float xRotationSpeed;
    private float yRotationSpeed;
    private float zRotationSpeed;
    private boolean isMeteorLike;
    private Quaternionf prevRotation = new Quaternionf();

    public PhysicsBlockComponent(BlockPhysicsEntity spinningBlock) {
        this.blockPhysicsEntity = spinningBlock;
    }

//    public void setBlocks(List<BlockPhysicsEntity.BlockData> blocks) {
//        sync();
//        this.blocks = blocks;
//    }
//
//    public void addBlock(BlockPhysicsEntity.BlockData blockData) {
//        this.blocks.add(blockData);
//        sync();
//    }
//
//    public List<BlockPhysicsEntity.BlockData> getBlocks() {
//        return blocks;
//    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = new Quaternionf(rotation);
        this.sync();
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public Vector3f getRotationSpeed() {
        return new Vector3f(this.xRotationSpeed, this.yRotationSpeed, this.zRotationSpeed);
    }

    public void setRotationSpeed(float xRotationSpeed, float yRotationSpeed, float zRotationSpeed) {
        this.xRotationSpeed = xRotationSpeed;
        this.yRotationSpeed = yRotationSpeed;
        this.zRotationSpeed = zRotationSpeed;
    }

    public boolean isMeteorLike() {
        return this.isMeteorLike;
    }
    public void setMeteorLike(boolean meteorLike) {
        isMeteorLike = meteorLike;
    }

    public Quaternionf getLerpedRotation(float tickDelta) {
        Quaternionf lerpedQuaternion = new Quaternionf();
        this.prevRotation.slerp(this.rotation, tickDelta, lerpedQuaternion);
        return lerpedQuaternion;
    }

    public void sync(){
        InitializeComponents.PHYSICS_BLOCK.sync(this.blockPhysicsEntity);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        boolean shouldSync = false;
        if (nbtCompound.contains("rotation")) {
            NbtCompound rotationNbt = nbtCompound.getCompound("rotation");
            this.prevRotation = new Quaternionf(this.rotation);
            this.rotation.set(
                    rotationNbt.getFloat("x"),
                    rotationNbt.getFloat("y"),
                    rotationNbt.getFloat("z"),
                    rotationNbt.getFloat("w")
            );
            shouldSync = true;
        }

        if (nbtCompound.contains("rotationSpeed")) {
            NbtCompound rotationSpeedNbt = nbtCompound.getCompound("rotationSpeed");
            this.xRotationSpeed = rotationSpeedNbt.getFloat("x");
            this.yRotationSpeed = rotationSpeedNbt.getFloat("y");
            this.zRotationSpeed = rotationSpeedNbt.getFloat("z");
            shouldSync = true;
        }

//        if (nbtCompound.contains("blocks")) {
//            NbtList blockList = nbtCompound.getList("blocks", NbtList.COMPOUND_TYPE);
//
//            this.setBlocks(
//                    blockList.stream().map((nbtElement) -> {
//                        NbtCompound blockNbt = (NbtCompound) nbtElement;
//                        return BlockPhysicsEntity.BlockData.fromNBT(blockNbt, wrapperLookup);
//                    }).toList()
//            );
//
//            shouldSync = true;
//        }

        if (shouldSync) {
            this.sync();
        }
    }


    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound rotationNbt = new NbtCompound();
        rotationNbt.putFloat("x", rotation.x);
        rotationNbt.putFloat("y", rotation.y);
        rotationNbt.putFloat("z", rotation.z);
        rotationNbt.putFloat("w", rotation.w);
        nbtCompound.put("rotation", rotationNbt);

        NbtCompound rotationSpeedNbt = new NbtCompound();
        rotationSpeedNbt.putFloat("x", xRotationSpeed);
        rotationSpeedNbt.putFloat("y", yRotationSpeed);
        rotationSpeedNbt.putFloat("z", zRotationSpeed);
        nbtCompound.put("rotationSpeed", rotationSpeedNbt);

//        NbtList blockList = new NbtList();
//        for (BlockPhysicsEntity.BlockData blockData : blocks) {
//            blockList.add(blockData.asNBT());
//        }
//
//        nbtCompound.put("blocks", blockList);
    }
}
