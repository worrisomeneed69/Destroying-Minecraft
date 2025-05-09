package com.sp.cca.custom;

import com.sp.cca.InitializeComponents;
import com.sp.entity.client.renderer.BlockType;
import com.sp.entity.custom.SpinningBlockEntity;
import com.sp.util.RandomUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

import java.util.List;

public class SpinningBlockComponent implements AutoSyncedComponent, ClientTickingComponent {
    private final SpinningBlockEntity spinningBlockEntity;
    private float pitchIncrement;
    private float yawIncrement;
    private float pitch;
    private float yaw;
    private float prevPitch;
    private float prevYaw;

    private float accelerationFactor;
    private Vector3f randDir;
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
        this.accelerationFactor = random.nextFloat()*0.05f + 0.1f;

        float spread = 0.5f;
        this.randDir = new Vector3f(-1 + RandomUtil.nextBetween(random, -spread, spread),  + RandomUtil.nextBetween(random, -spread, spread), -1 + RandomUtil.nextBetween(random, -spread, spread));

        this.scale = 1;

        BlockType[] values = BlockType.values();
        this.blockType = values[random.nextBetween(0, values.length - 1)];
//        this.blockType = BlockType.COW;
    }


    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }
    public BlockState getBlockState() {
        return this.blockState;
    }

    public float getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevYaw, yaw);
    }
    public float getPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPitch, pitch);
    }

    public float getAccelerationFactor() {
        return this.accelerationFactor;
    }
    public Vector3f getRandDir(){
        return this.randDir;
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

        this.randDir.x = nbtCompound.getFloat("randDirX");
        this.randDir.y = nbtCompound.getFloat("randDirY");
        this.randDir.z = nbtCompound.getFloat("randDirZ");
    }


    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("blockState", NbtHelper.fromBlockState(this.blockState));
        nbtCompound.putFloat("pitchIncrement", this.pitchIncrement);
        nbtCompound.putFloat("yawIncrement", this.yawIncrement);
        nbtCompound.putFloat("accelerationFactor", this.accelerationFactor);
        nbtCompound.putFloat("scale", this.scale);

        nbtCompound.putFloat("randDirX", this.randDir.x);
        nbtCompound.putFloat("randDirY", this.randDir.y);
        nbtCompound.putFloat("randDirZ", this.randDir.z);
    }

    @Override
    public void clientTick() {
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;

        if(!this.spinningBlockEntity.isOnGround()) {
            this.pitch += this.pitchIncrement;
            this.yaw += this.yawIncrement;
        }
    }
}