package com.sp.cca.custom;

import com.sp.cca.InitializeComponents;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.List;

public class SpinningBlockComponent implements AutoSyncedComponent {
    private final SpinningBlockEntity spinningBlockEntity;
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
        this.blockState = randomBlocks.get( spinningBlock.getRandom().nextBetween(0, randomBlocks.size() - 1) );

//        this.sync();
    }

    public BlockState getBlockState() {
        return this.blockState;
    }


    public void sync(){
        InitializeComponents.SPINNING_BLOCK.sync(this.spinningBlockEntity);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.blockState = NbtHelper.toBlockState(wrapperLookup.getWrapperOrThrow(RegistryKeys.BLOCK),  nbtCompound.getCompound("blockState"));
    }


    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("blockState", NbtHelper.fromBlockState(this.blockState));
    }
}
