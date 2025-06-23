package com.sp.cca.custom;

import com.sp.cca.InitializeComponents;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import org.joml.Quaternionf;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.List;

public class PhysicsBlockComponent implements AutoSyncedComponent {
    private final BlockPhysicsEntity blockPhysicsEntity;
    private List<BlockPhysicsEntity.BlockData> blocks = new ArrayList<>();
    private Quaternionf rotation = new Quaternionf();

    public PhysicsBlockComponent(BlockPhysicsEntity spinningBlock) {
        this.blockPhysicsEntity = spinningBlock;
    }

    public void setBlocks(List<BlockPhysicsEntity.BlockData> blocks) {
        sync();
        this.blocks = blocks;
    }

    public void addBlock(BlockPhysicsEntity.BlockData blockData) {
        this.blocks.add(blockData);
        sync();
    }

    public List<BlockPhysicsEntity.BlockData> getBlocks() {
        return blocks;
    }

    public void setRotation(Quaternionf rotation) {
        this.sync();
        this.rotation = rotation;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void sync(){
        InitializeComponents.PHYSICS_BLOCK.sync(this.blockPhysicsEntity);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains("rotation")) {
            NbtCompound rotationNbt = nbtCompound.getCompound("rotation");
            this.rotation.set(
                    rotationNbt.getFloat("x"),
                    rotationNbt.getFloat("y"),
                    rotationNbt.getFloat("z"),
                    rotationNbt.getFloat("w")
            );
            this.sync();
        }

        if (nbtCompound.contains("blocks")) {
            NbtList blockList = nbtCompound.getList("blocks", NbtList.COMPOUND_TYPE);

            this.setBlocks(
                    blockList.stream().map((nbtElement) -> {
                        NbtCompound blockNbt = (NbtCompound) nbtElement;
                        return BlockPhysicsEntity.BlockData.fromNBT(blockNbt, wrapperLookup);
                    }).toList()
            );
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

        NbtList blockList = new NbtList();
        for (BlockPhysicsEntity.BlockData blockData : blocks) {
            blockList.add(blockData.asNBT());
        }

        nbtCompound.put("blocks", blockList);
    }
}
