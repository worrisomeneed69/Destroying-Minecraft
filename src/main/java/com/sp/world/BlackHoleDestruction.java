package com.sp.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.util.MathUtil;
import com.sp.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackHoleDestruction {
    private static Box selection;
    private static List<BlockPos> surfaceBlocks;
    private static final Map<BlockPos, BlockState> resetBlockMap = new HashMap<>();
    private static final List<BlockPhysicsEntity> resetEntityMap = new ArrayList<>();
    private static boolean settingSelection;
    private static boolean startDestruction;
    private static int breakOffCooldown;
    private static final Random random = Random.create();

    public static void tick(World world) {
        if(!startDestruction) return;

        if (breakOffCooldown <= 0) {
            List<BlockPos> breakOffList = new ArrayList<>();

            BlockPos randomSurfacePos = MathUtil.randomValueInList(surfaceBlocks);
            breakOffList.add(randomSurfacePos);

            int size = random.nextBetween(1, 4);

            BlockPos.Mutable mutable = randomSurfacePos.mutableCopy();
            for (int i = 0; i < size; i++) {
                Direction randDir = MathUtil.randomValueInList(Direction.values());

                if(randDir == Direction.UP) continue;
                BlockPos pos = mutable.offset(randDir);

                breakOffList.add(pos);
                mutable.set(pos);
            }

            for (BlockPos pos : breakOffList) {
                resetBlockMap.put(pos, world.getBlockState(pos));
            }

            resetEntityMap.add(breakOff(world, breakOffList));
            breakOffCooldown = random.nextBetween(40, 80);
        } else {
            breakOffCooldown--;
        }
    }

    public static void renderSelectionDebug(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera) {
        Box tempSelection = selection;
        if (selection != null) {
            matrices.push();
            Vec3d cameraPos = camera.getPos();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            //Blinking
            int alpha = (int) ((Math.sin(RenderSystem.getShaderGameTime()*2000) * 0.5 + 0.5) * 100) + 50;

            //Draw center box
            RenderUtil.drawBox(matrices, vertexConsumers, selection.getCenter(), new Vec3d(1, 2, 1), 0, 0, 255, 150);

            //Highlight all the selected blocks
            if (surfaceBlocks != null && !settingSelection) {
                for (BlockPos surfaceBlock : surfaceBlocks) {
                    if(surfaceBlock == null) continue;
                    RenderUtil.drawBox(matrices, vertexConsumers, surfaceBlock.toCenterPos(), 1, 20, 200, 20, alpha);
                }
            }

            matrices.pop();
        }

        selection = tempSelection;
    }

    /**
     * Selects an area of blocks set to break off into physics blocks and fly into the black hole
     * @param pos The center block pos
     * @return The number of blocks selected
     */
    public static int setSelection(BlockPos pos, World world) {
        settingSelection = true;

        surfaceBlocks = new ArrayList<>();
        selection = Box.enclosing(pos, pos).expand(20, 0, 20);

        //Find all the blocks on the surface and blocks that are visible enough to the sun
        BlockPos.stream(selection.withMaxY(selection.maxY - 0.5).withMaxX(selection.maxX - 0.5).withMaxZ(selection.maxZ - 0.5)).forEachOrdered(blockPos -> {
            BlockPos.Mutable standInBlockPos = blockPos.mutableCopy();
            BlockPos.Mutable standInBlockPos2 = blockPos.mutableCopy();
            BlockPos surfaceBlock = null;

            //Find the highest block above the current block
            while (true) {
                if (world.getBlockState(standInBlockPos.up()) == Blocks.AIR.getDefaultState()) {
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
                if (world.getBlockState(blockPos) == Blocks.AIR.getDefaultState()) {
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

                if (world.getBlockState(belowPos) == Blocks.AIR.getDefaultState()) {
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
                        surfaceBlock = blockHitResultDown.getBlockPos();
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

        settingSelection = false;

        return surfaceBlocks.size();
    }

    //PSEUDOCODE FOR ABOVE
    /*
        while loop {
            if (Block above is air) {
                Cast a ray up

                if (ray hits something) {
                    save blockPos
                    loop back to beginning
                } else {
                    return saved block pos or no blocks are above
                }

            } else {
                Go up by one block
                Save Block pos
                Loop back to beginning
            }
        }

        if (Nothing above was hit) {
            if (current block is air) {
                Cast a ray down

                if (ray hit something) {
                    That block is the highest
                } else {
                    There's really nothing then. Return original block pos
                }
            } else {
                Then the current block is the highest one. use it
            }
        }

        iterate below and check if there are more exposed blocks
     */

    public static void reset(World world) {
//        resetBlockMap.forEach(world::setBlockState);

        resetEntityMap.forEach(blockPhysicsEntity -> {
            if(blockPhysicsEntity != null && !blockPhysicsEntity.isRemoved()){
                blockPhysicsEntity.discard();
            }
        });
    }

    public static BlockPhysicsEntity breakOff(World world, List<BlockPos> blocks) {
        return BlockPhysicsEntity.ofBlocks(world, blocks);
    }

    public static void setStartDestruction(boolean bl) {
        startDestruction = bl;
    }

    public static void clear() {
        selection = null;
        surfaceBlocks.clear();
    }


}
