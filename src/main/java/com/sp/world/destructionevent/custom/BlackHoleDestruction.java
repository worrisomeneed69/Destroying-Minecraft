package com.sp.world.destructionevent.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.sounds.ModSounds;
import com.sp.util.MathUtil;
import com.sp.util.RenderUtil;
import foundry.veil.api.client.util.Easing;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlackHoleDestruction {
    private static Box selection;
    private static List<BlockPos> surfaceBlocks;
    private static final List<BlockPhysicsEntity> resetEntityMap = new ArrayList<>();
    private static boolean startDestruction;
    private static int breakOffCooldown;
    private static int aggressiveCooldown;
    private static final Random random = Random.create();
    private static final PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(random);


    public static void tick(World world) {
        if (!startDestruction || surfaceBlocks.isEmpty()) return;

        if (breakOffCooldown <= 0) {

            if (aggressiveCooldown <= 0) {
                for (PlayerEntity player : world.getPlayers()) {
                    if (player.isCreative() || player.isSpectator()) continue;

                    BlockPos standingBlockPos = player.isOnGround()
                        ? player.getBlockPos().down()
                        : world.raycast(
                            new RaycastContext(
                                player.getPos(),
                                player.getPos().add(0, -50, 0),
                                RaycastContext.ShapeType.COLLIDER,
                                RaycastContext.FluidHandling.NONE,
                                ShapeContext.absent()
                            )
                        ).getBlockPos();

                    collectAndBreakOff(world, standingBlockPos);
                }
                aggressiveCooldown = random.nextBetween(100, 200);
            } else {
                collectAndBreakOff(world, MathUtil.randomValueInList(surfaceBlocks));
            }

            breakOffCooldown = random.nextBetween(5, 8);
//            breakOffCooldown = random.nextBetween(20, 30);
        } else {
            breakOffCooldown--;
            aggressiveCooldown--;
        }
    }

    private static void collectAndBreakOff(World world, BlockPos startingBlockPos) {
        List<BlockPos> breakOffList;
        switch (random.nextBetween(1, 2)) {
            case 2 -> breakOffList = getIsland(world, startingBlockPos);
            default -> breakOffList = getNoodle(world, startingBlockPos);
        }

        if (!breakOffList.isEmpty()) breakOff(world, breakOffList);
    }

    private static List<BlockPos> getNoodle(World world, BlockPos startPos) {
        List<BlockPos> breakOffList = new ArrayList<>();


        BlockPos.Mutable mutable = startPos.mutableCopy();
        breakOffList.add(mutable);

        int size = random.nextBetween(3, 10);

        for (int i = 0; i < size; i++) {
            Direction randDir = MathUtil.randomValueInList(Direction.values());

            if(randDir == Direction.UP) continue;
            BlockPos pos = mutable.offset(randDir);

            if (world.getBlockState(pos).isSolid()) {
                breakOffList.add(pos);
                surfaceBlocks.remove(pos);
                mutable.set(pos);
            }
        }

        return breakOffList;
    }

    private static List<BlockPos> getPerlinNoise(World world, BlockPos startPos) {
        List<BlockPos> breakOffList = new ArrayList<>();

        int range = random.nextBetween(3, 8);
        for (int i = range; i > 0; i--) {


            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos selectedPos = startPos.add(x, 0, z);

                    Vec3d pos = selectedPos.toCenterPos().multiply(0.2);
                    double noise = (noiseSampler.sample(pos.x, pos.y, pos.z) * 2.0 - 1.0) * 5;

                    if (noise > 0.4 && world.getBlockState(selectedPos).isSolid()) {
                        breakOffList.add(selectedPos);
                        surfaceBlocks.remove(selectedPos);
                    }
                }
            }
        }

        return breakOffList;
    }

    private static List<BlockPos> getIsland(World world, BlockPos startPos) {
        List<BlockPos> breakOffList = new ArrayList<>();

        int radius = random.nextBetween(1, 3);
        for (int i = radius; i > 0; i--) {
            BlockPos centerBlockPos = startPos.add(0, -(radius - i), 0);

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos selectedPos = centerBlockPos.add(x, 0, z);

                    Vec3d centerPos = centerBlockPos.toCenterPos();
                    Vec3d centerDirection = centerPos.subtract(selectedPos.toCenterPos()).normalize();
                    Vec3d adjustedSelectedPos = selectedPos.toCenterPos().subtract(centerDirection.multiply(Easing.EASE_OUT_QUINT.ease((float) (radius - i) / radius)));
                    boolean isInRange = adjustedSelectedPos.isInRange(centerPos, i);

                    if (isInRange && world.getBlockState(selectedPos).isSolid()) {
                        breakOffList.add(centerBlockPos.add(x, 0, z));
                        surfaceBlocks.remove(selectedPos);
                    }
                }
            }
        }

        return breakOffList;
    }

    public static void renderSelectionDebug(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, Frustum frustum) {
        if (selection == null) return;

        matrices.push();
        Vec3d cameraPos = camera.getPos();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        //Blinking
        int alpha = (int) ((Math.sin(RenderSystem.getShaderGameTime()*2000) * 0.5 + 0.5) * 100) + 50;

        //Draw center box
        RenderUtil.drawBox(matrices, vertexConsumers, selection.getCenter(), new Vec3d(1, 2, 1), 0, 0, 255, 150, false, false);

        //Highlight all the selected blocks
        ArrayList<BlockPos> listCopy = new ArrayList<>(surfaceBlocks);
            for (BlockPos surfaceBlock : listCopy) {
                if(surfaceBlock == null) continue;
                Vec3d pos = surfaceBlock.toCenterPos();
                boolean isVisible = frustum.isVisible(Box.of(pos, 1, 1, 1));
                if (isVisible) RenderUtil.drawBox(matrices, vertexConsumers, pos, 1, 20, 200, 20, alpha, false, false);
            }
        matrices.pop();
    }

    /**
     * Selects an area of blocks set to break off into physics blocks and fly into the black hole
     * @param pos The center block pos
     * @return The number of blocks selected
     */
    public static int selectSurfaceBlocks(BlockPos pos, World world) {
        surfaceBlocks = new ArrayList<>();
        selection = Box.enclosing(pos, pos).expand(100, 0, 100);

        //Finds all the blocks on the surface and blocks that are visible enough to the sun
        BlockPos.stream(selection.withMaxY(selection.maxY - 0.5).withMaxX(selection.maxX - 0.5).withMaxZ(selection.maxZ - 0.5)).forEachOrdered(blockPos -> {
            BlockPos.Mutable standInBlockPos = blockPos.mutableCopy();
            BlockPos.Mutable standInBlockPos2 = blockPos.mutableCopy();
            BlockPos surfaceBlock = null;

            //Find the highest block above the current block
            while (true) {
                if (!world.getBlockState(standInBlockPos.up()).isSolid()) {
                    BlockHitResult blockHitResultUp = world.raycast(
                            new RaycastContext(
                                    standInBlockPos.toBottomCenterPos().add(0, 1.1, 0),
                                    standInBlockPos.toBottomCenterPos().add(0, 20, 0),
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    ShapeContext.absent()
                            )
                    );

                    if (blockHitResultUp.getType() == BlockHitResult.Type.BLOCK) {
                        surfaceBlock = blockHitResultUp.getBlockPos();
                        standInBlockPos.set(blockHitResultUp.getBlockPos());
                    } else {
                        break;
                    }
                } else {
                    surfaceBlock = standInBlockPos.up();
                    standInBlockPos.set(standInBlockPos.up());
                }

                if (standInBlockPos.getY() > 300) {
                    break;
                }
            }

            //If no block was found above, check below
            if(surfaceBlock == null){
                if (!world.getBlockState(blockPos).isSolid()) {
                    BlockHitResult blockHitResultDown = world.raycast(
                            new RaycastContext(
                                    blockPos.toBottomCenterPos(),
                                    blockPos.toBottomCenterPos().add(0, -50, 0),
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    ShapeContext.absent()
                            )
                    );

                    if (blockHitResultDown.getType() == BlockHitResult.Type.BLOCK) {
                        surfaceBlock = blockHitResultDown.getBlockPos();
                    } else {
                        surfaceBlock = standInBlockPos2;
                    }
                } else {
                    surfaceBlock = standInBlockPos2;
                }
            }

            surfaceBlocks.add(surfaceBlock);

            //Now iterate below the block to see if any blocks below are exposed to the surface
            for (int i = 1; i <= 11; i++) {
                BlockPos belowPos = surfaceBlock.down();

                if (!world.getBlockState(belowPos).isSolid()) {
                    BlockHitResult blockHitResultDown = world.raycast(
                            new RaycastContext(
                                    belowPos.toBottomCenterPos(),
                                    belowPos.toCenterPos().add(0, -50, 0),
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    ShapeContext.absent()
                            )
                    );

                    if (blockHitResultDown.getType() == BlockHitResult.Type.BLOCK) {
                        belowPos = blockHitResultDown.getBlockPos();
//                        surfaceBlock = blockHitResultDown.getBlockPos();
                    } else {
                        break; //No blocks below
                    }
                }

                for (Direction direction : Direction.values()) {
                    BlockPos lightTestPos = belowPos.offset(direction);

                    if (world.getLightLevel(LightType.SKY, lightTestPos) >= 10) {
                        surfaceBlocks.add(belowPos);
                        break;
                    }

                }

                surfaceBlock = belowPos;
            }

        });

        return surfaceBlocks.size();
    }

    public static void reset() {
        resetEntityMap.forEach(blockPhysicsEntity -> {
            if(blockPhysicsEntity != null && !blockPhysicsEntity.isRemoved()){
                blockPhysicsEntity.discard();
            }
        });
    }

    private static void breakOff(World world, List<BlockPos> blocks) {
        BlockPhysicsEntity entity = BlockPhysicsEntity.ofBlocks(world, blocks);
        int shouldBeMeteor = random.nextBetween(1, 5);

        if (shouldBeMeteor > 1) {
            entity.setVelocity(0, 0.06, -0.2);
            entity.component.setRotationSpeed(
                    MathUtil.nextBetween(0.1f, 0.8f),
                    MathUtil.nextBetween(0.1f, 0.8f),
                    MathUtil.nextBetween(0.1f, 0.8f)
            );
            entity.component.sync();
            entity.velocityDirty = true;
            entity.velocityModified = true;

            entity.playSound(ModSounds.BREAK_OFF, 8.0f, MathUtil.nextBetween(0.5f, 1.5f));
        } else {
            Vec3d inComingDirection = new Vec3d(-0.5, -0.5, 0.1).normalize();
            entity.setPosition(entity.getPos().add(inComingDirection.negate().multiply(100)));
            entity.setVelocity(inComingDirection.multiply(3));
            entity.component.setMeteorLike(true);
            entity.component.setRotationSpeed(
                    MathUtil.nextBetween(0.8f, 1.5f),
                    MathUtil.nextBetween(0.8f, 1.5f),
                    MathUtil.nextBetween(0.8f, 1.5f)
            );
            entity.component.sync();
        }

        resetEntityMap.add(entity);
    }

    public static void setStartDestruction(boolean bl) {
        startDestruction = bl;
    }

    public static void clear() {
        selection = null;
        surfaceBlocks.clear();
    }


}
