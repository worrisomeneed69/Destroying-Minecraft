package com.sp.entity.custom;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.PhysicsBlockComponent;
import com.sp.collision.BlockOBB;
import com.sp.entity.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockPhysicsEntity extends Entity {
    public PhysicsBlockComponent component;

    public static BlockPhysicsEntity ofBlocks(World world, List<BlockPos> blocks) {
        BlockPhysicsEntity entity = new BlockPhysicsEntity(ModEntities.BLOCK_PHYSICS_ENTITY, world);
        PhysicsBlockComponent component = InitializeComponents.PHYSICS_BLOCK.get(entity);
        Vec3d pos = blocks.getFirst().toCenterPos();
        entity.setPosition(pos);

        for (BlockPos blockPos : blocks) {
            BlockState state = world.getBlockState(blockPos);
            BlockPos relativePos = blockPos.subtract(blocks.getFirst());

            if (!state.isAir()) {
                component.addBlock(new BlockPhysicsEntity.BlockData(state, relativePos));
            }

            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        }

        world.spawnEntity(entity);
        return entity;
    }

    public BlockPhysicsEntity(EntityType<?> type, World world) {
        super(type, world);

        this.component = InitializeComponents.PHYSICS_BLOCK.get(this);

        /*
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(0, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(0, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(0, 0, 2)));

        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(1, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(1, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(1, 0, 2)));

        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(2, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(2, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(2, 0, 2)));

        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(3, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(3, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(3, 0, 2)));

        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(4, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(4, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(4, 0, 2)));

        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(5, 0, 0)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(5, 0, 1)));
        this.component.addBlock(new BlockData(Blocks.GRASS_BLOCK, new BlockPos(5, 0, 2)));
        */
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    public void tick() {
        super.tick();

        this.setBoundingBox(this.calculateBoundingBox());

//        this.setVelocity(0, 0.03, -0.05);

        if(!this.getWorld().isClient) {
//            this.component.setRotation(this.component.getRotation().rotateLocalX((float) Math.toRadians(5f)));
        }
//        this.component.getRotation().rotateLocalX((float) Math.toRadians(1f));
//        this.component.setRotation(new Quaternionf(0, 0, 0, 0).rotationXYZ(0, 0, (float) Math.toRadians(0f)));

        this.move();
    }

    public List<BlockOBB.CollisionData> getAllCollisions(Box aabb) {

        List<BlockOBB.CollisionData> collisions = new ArrayList<>();

        for (BlockData block : this.component.getBlocks()) {
            BlockOBB obb = new BlockOBB(this.component.getRotation(), block);

            BlockOBB.CollisionData collisionData = obb.getMinCollisionWith(aabb, this);
            if (collisionData.collides()) {
                collisions.add(collisionData);
            }
        }

        return collisions;
    }

    public double getYAxisCollision(Box aabb) {
        double bestOffset = 0;

        for (BlockData block : this.component.getBlocks()) {
            BlockOBB obb = new BlockOBB(this.component.getRotation(), block);

            BlockOBB.CollisionData collisionData = obb.getMinCollisionWith(aabb.offset(new Vec3d(0, bestOffset, 0)), this);
            if (collisionData.collides() && collisionData.overLapp() > 0) {
                bestOffset += collisionData.overLapp();
            }
        }

        return bestOffset;
    }

    public Vec3d getBestCollisionOffset(Box aabb, Vec3d movement) {
        Vec3d offset = movement;

        for (BlockData block : this.component.getBlocks()) {
            BlockOBB obb = new BlockOBB(this.component.getRotation(), block);

            //getStepHeight()
            BlockOBB.CollisionData collisionData = obb.getMinCollisionWith(aabb.offset(offset), this);
            if (collisionData.collides()) {
                Vec3d resolveAxis = collisionData.axis();

                // Ensure the axis points away from collision
                double dot = movement.dotProduct(resolveAxis);
                if (dot > 0) {
                    resolveAxis = resolveAxis.negate();
                }

                offset = offset.add(resolveAxis.multiply(collisionData.overLapp()));
            }
        }

        return offset;
    }

    public boolean collides(Box aabb) {
        for (BlockData block : this.component.getBlocks()) {
            BlockOBB obb = new BlockOBB(this.component.getRotation(), block);

            if (obb.collidesWith(aabb, this)) {
                return true;
            }
        }

        return false;
    }

    public void move() {
        this.setPosition(this.getPos().add(this.getVelocity()));
    }

    public static Vec3d toVec3(Vector3d vector3d) {
        return new Vec3d(vector3d.x(), vector3d.y(), vector3d.z());
    }

    public static Vector3d toVector3d(Vec3d vec3d) {
        return new Vector3d(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected Box calculateBoundingBox() {
        if (this.component == null || this.component.getBlocks() == null || this.component.getBlocks().isEmpty()) {
            return super.calculateBoundingBox();
        }

        Vec3d min = new Vec3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Vec3d max = new Vec3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (BlockData blockData : this.component.getBlocks()) {
            // Calculate the corners of the block (considering it's 1x1x1)
            for (double dx = 0; dx <= 1; dx++) {
                for (double dy = 0; dy <= 1; dy++) {
                    for (double dz = 0; dz <= 1; dz++) {
                        // Create a vector for each corner of the block
                        Vec3d corner = new Vec3d(
                                blockData.offset.getX() + dx - .5,
                                blockData.offset.getY() + dy - .5,
                                blockData.offset.getZ() + dz - .5
                        );

                        // Rotate the corner
                        Vec3d rotatedCorner = rotateVector(corner, this.component.getRotation());

                        // Update min/max
                        min = new Vec3d(
                                Math.min(min.x, rotatedCorner.x),
                                Math.min(min.y, rotatedCorner.y),
                                Math.min(min.z, rotatedCorner.z)
                        );

                        max = new Vec3d(
                                Math.max(max.x, rotatedCorner.x),
                                Math.max(max.y, rotatedCorner.y),
                                Math.max(max.z, rotatedCorner.z)
                        );
                    }
                }
            }
        }

        // Adjust min/max with entity position
        min = min.add(this.getPos());
        max = max.add(this.getPos());

        // Return the bounding box
        return new Box(min, max);//.expand(2);
    }

    // Helper method to apply quaternion rotation to a vector
    private Vec3d rotateVector(Vec3d vec, Quaternionf quaternion) {
        Vector3d result = quaternion.transform(new Vector3d(vec.x, vec.y, vec.z));

        return new Vec3d(result.x, result.y, result.z);
    }

    public static class BlockData {
        public BlockState blockState;
        public BlockPos offset;

        public PacketByteBuf buf;

        public BlockData(BlockState blockState, BlockPos offset) {
            this.blockState = blockState;
            this.offset = offset;
        }

        public BlockData(PacketByteBuf buf) {
            this.buf = buf;
        }

        public NbtCompound asNBT() {
            NbtCompound nbt = new NbtCompound();
            nbt.put("block", NbtHelper.fromBlockState(this.blockState));
            nbt.putInt("offsetX", offset.getX());
            nbt.putInt("offsetY", offset.getY());
            nbt.putInt("offsetZ", offset.getZ());
            return nbt;
        }

        public static BlockData fromNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
            NbtCompound blockStateCompound = nbt.getCompound("block");
            Optional<RegistryWrapper.Impl<Block>> wrapper = wrapperLookup.getOptionalWrapper(RegistryKeys.BLOCK);

            BlockState blockState1 = NbtHelper.toBlockState(wrapper.orElse(Registries.BLOCK.getReadOnlyWrapper()), blockStateCompound);

            BlockPos offset = new BlockPos(nbt.getInt("offsetX"), nbt.getInt("offsetY"), nbt.getInt("offsetZ"));
            return new BlockData(blockState1, offset);
        }
    }
}
