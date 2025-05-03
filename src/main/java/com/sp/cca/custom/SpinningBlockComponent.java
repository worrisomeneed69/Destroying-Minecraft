package com.sp.cca.custom;

import com.sp.cca.InitializeComponents;
import com.sp.entity.client.renderer.BlockType;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.random.Random;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.List;

public class SpinningBlockComponent implements AutoSyncedComponent {
    private final SpinningBlockEntity spinningBlockEntity;
    private float pitchIncrement;
    private float yawIncrement;
    private float accelerationFactor;
    private float scale;
    private final BlockType blockType;
    private BlockState blockState;

    private static final List<BlockState> randomBlocks = List.of(
            Blocks.DIRT.getDefaultState(),
            Blocks.STONE.getDefaultState(),
            Blocks.GRAVEL.getDefaultState(),
            Blocks.DEEPSLATE.getDefaultState(),
            Blocks.GRASS_BLOCK.getDefaultState()
    );


    public SpinningBlockComponent(SpinningBlockEntity spinningBlock) {
        this.spinningBlockEntity = spinningBlock;

        Random random = spinningBlock.getRandom();
        this.blockState = randomBlocks.get( random.nextBetween(0, randomBlocks.size() - 1) );
        this.pitchIncrement = random.nextFloat()*20;
        this.yawIncrement = random.nextFloat()*20;
        this.accelerationFactor = random.nextFloat()*0.05f + 0.3f;
        this.scale = random.nextFloat() + 1;

        BlockType[] values = BlockType.values();
        this.blockType = values[random.nextBetween(0, values.length - 1)];
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public float getYawIncrement() {
        return this.yawIncrement;
    }
    public float getPitchIncrement() {
        return this.pitchIncrement;
    }
    public float getAccelerationFactor() {
        return this.accelerationFactor;
    }
    public float getScale() {
        return scale;
    }
    public BlockType getBlockType() {
        return blockType;
    }


    public void sync(){
        InitializeComponents.SPINNING_BLOCK.sync(this.spinningBlockEntity);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.blockState = NbtHelper.toBlockState(wrapperLookup.getWrapperOrThrow(RegistryKeys.BLOCK),  nbtCompound.getCompound("blockState"));
        this.pitchIncrement = nbtCompound.getFloat("pitchIncrement");
        this.yawIncrement = nbtCompound.getFloat("yawIncrement");
        this.accelerationFactor = nbtCompound.getFloat("accelerationFactor");
        this.scale = nbtCompound.getFloat("scale");
    }


    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("blockState", NbtHelper.fromBlockState(this.blockState));
        nbtCompound.putFloat("pitchIncrement", this.pitchIncrement);
        nbtCompound.putFloat("yawIncrement", this.yawIncrement);
        nbtCompound.putFloat("accelerationFactor", this.accelerationFactor);
        nbtCompound.putFloat("scale", this.scale);
    }
}
