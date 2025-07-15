package com.sp.cca.custom.entity;

import com.sp.cca.InitializeComponents;
import com.sp.entity.client.renderer.BlockType;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
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
    private Vec3d acceleration;
    private boolean applyGravity;
    private int lifeTime;

    private float accelerationFactor;
    private float scale;
    private final BlockType blockType;
    private BlockState blockState;


    private static final List<BlockState> randomBlocks = List.of(
            Blocks.DIRT.getDefaultState(),
            Blocks.STONE.getDefaultState(),
            Blocks.GRAVEL.getDefaultState(),
            Blocks.OAK_LEAVES.getDefaultState(),
            Blocks.OAK_LOG.getDefaultState(),
            Blocks.GRASS_BLOCK.getDefaultState()
    );


    public SpinningBlockComponent(SpinningBlockEntity spinningBlock) {
        this.spinningBlockEntity = spinningBlock;

        Random random = spinningBlock.getRandom();
        this.blockState = randomBlocks.get( random.nextBetween(0, randomBlocks.size() - 1) );
//        this.blockState = Blocks.DIRT.getDefaultState();
        this.pitchIncrement = random.nextFloat()*20;
        this.yawIncrement = random.nextFloat()*20;
        this.accelerationFactor = random.nextFloat()*0.05f + 0.1f;

        this.scale = 1;

//        BlockType[] values = BlockType.values();
//        this.blockType = values[random.nextBetween(0, values.length - 1)];
        this.blockType = BlockType.SINGLE;
        this.acceleration = Vec3d.ZERO;
        this.applyGravity = true;
        this.lifeTime = 100;
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
    public float getScale() {
        return scale;
    }
    public BlockType getBlockType() {
        return blockType;
    }

    public boolean shouldApplyGravity() {
        return applyGravity;
    }
    public void setApplyGravity(boolean applyGravity) {
        this.applyGravity = applyGravity;
    }

    public int getLifeTime() {
        return lifeTime;
    }
    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Vec3d getAcceleration() {
        return acceleration;
    }
    public void setAcceleration(Vec3d acceleration) {
        this.acceleration = acceleration;
    }
    public void setAcceleration(float xAcceleration, float yAcceleration, float zAcceleration) {
        this.acceleration = new Vec3d(xAcceleration, yAcceleration, zAcceleration);
    }


    public void sync() {
        InitializeComponents.SPINNING_BLOCK.sync(this.spinningBlockEntity);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.blockState = NbtHelper.toBlockState(wrapperLookup.getWrapperOrThrow(RegistryKeys.BLOCK),  nbtCompound.getCompound("blockState"));
        this.pitchIncrement = nbtCompound.getFloat("pitchIncrement");
        this.yawIncrement = nbtCompound.getFloat("yawIncrement");
        this.accelerationFactor = nbtCompound.getFloat("accelerationFactor");
        this.scale = nbtCompound.getFloat("scale");
        this.acceleration = new Vec3d(
                nbtCompound.getFloat("accelerationX"),
                nbtCompound.getFloat("accelerationY"),
                nbtCompound.getFloat("accelerationZ")
        );
        this.applyGravity = nbtCompound.getBoolean("applyGravity");
    }


    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("blockState", NbtHelper.fromBlockState(this.blockState));
        nbtCompound.putFloat("pitchIncrement", this.pitchIncrement);
        nbtCompound.putFloat("yawIncrement", this.yawIncrement);
        nbtCompound.putFloat("accelerationFactor", this.accelerationFactor);
        nbtCompound.putFloat("scale", this.scale);

        nbtCompound.putFloat("accelerationX", (float) this.acceleration.x);
        nbtCompound.putFloat("accelerationY", (float) this.acceleration.y);
        nbtCompound.putFloat("accelerationZ", (float) this.acceleration.z);
        nbtCompound.putBoolean("applyGravity", this.applyGravity);
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