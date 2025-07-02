package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SelectionHandler {
    private static boolean renderingSelection;
    private static SelectionFunction selectionFunction;
    private static BlockPos corner1;
    private static BlockPos corner2;
    private static BlockHitResult targetBlock;
    private static int delayTime = 10;


    public static void startSelection(SelectionFunction function) {
        renderingSelection = true;
        selectionFunction = function;
    }


    public static void tickClientWorld(World world) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!renderingSelection) return;
        client.gameRenderer.setBlockOutlineEnabled(false);
        if (delayTime > 0) {
            delayTime--;
            return;
        }


        if (corner1 == null && targetBlock != null) {
            if (client.options.useKey.isPressed()) {
                //Selected first corner
                corner1 = targetBlock.getBlockPos();
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1, 0.1f);
                delayTime = 10;
                return;
            }
        }

        if (corner1 != null && corner2 == null) {
            if (client.options.useKey.isPressed()) {
                //Selected second corner
                corner2 = targetBlock.getBlockPos();
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1, 1.0f);
            }
        }

        if (corner1 != null && corner2 != null) {
            executeSelectionFunction();
            end();
            client.gameRenderer.setBlockOutlineEnabled(true);
        }


    }

    public static void renderSelection(MatrixStack matrices, VertexConsumerProvider vertexConsumers, RenderTickCounter renderTickCounter, Camera camera) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!renderingSelection || client.player == null) return;

        targetBlock = (BlockHitResult) client.player.raycast(50, renderTickCounter.getTickDelta(true), false);
        if (targetBlock.getType() != HitResult.Type.BLOCK) return;

        int alpha = (int) ((Math.sin(RenderSystem.getShaderGameTime()*2000) * 0.5 + 0.5) * 100) + 50;

        if (corner1 == null) {
            Vec3d blockPos = targetBlock.getBlockPos().toCenterPos();
            RenderUtil.drawBox(matrices, vertexConsumers, blockPos.subtract(camera.getPos()), 1, 100, 255, 100, alpha);
        } else if(corner2 == null) {
            RenderUtil.drawBlocksFromCorners(matrices, vertexConsumers, camera, corner1, targetBlock.getBlockPos(), 100, 255, 100, alpha);
        }
    }


    private static void executeSelectionFunction() {
        selectionFunction.runFunction(corner1, corner2);
    }

    private static void end() {
        corner1 = null;
        corner2 = null;
        renderingSelection = false;
        selectionFunction = null;
    }


    public boolean isRenderingSelection() {
        return renderingSelection;
    }

    public void setRenderingSelection(boolean renderingSelection) {
        SelectionHandler.renderingSelection = renderingSelection;
    }


    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface SelectionFunction {
        void runFunction(BlockPos corner1, BlockPos corner2);
    }
}
