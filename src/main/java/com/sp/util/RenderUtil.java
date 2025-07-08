package com.sp.util;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RenderUtil {
    //TODO put draw selection from BlackHoleDestruction in here (Input a list of blocks. Renders a box for each block)

    public static void drawBlocksFromCorners(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, BlockPos corner1, BlockPos corner2, int red, int green, int blue, int alpha, boolean renderOutline) {
        if(camera != null) matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        Box box = Box.enclosing(corner1, corner2);
        drawBox(matrices, vertexConsumers, box, red, green, blue, alpha, renderOutline);
    }

    public static void drawBlocksFromCorners(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, Vec3d corner1, Vec3d corner2, int red, int green, int blue, int alpha, boolean renderOutline) {
        if(camera != null) matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        Box box = new Box(corner1, corner2);
        drawBox(matrices, vertexConsumers, box, red, green, blue, alpha, renderOutline);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d targetPos, double size, int red, int green, int blue, int alpha, boolean renderOutline) {
        drawBox(matrices, vertexConsumers, targetPos, new Vec3d(size, size, size), red, green, blue, alpha, renderOutline);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d targetPos, Vec3d size, int red, int green, int blue, int alpha, boolean renderOutline) {
        Box box = Box.of(targetPos, size.x, size.y, size.z);
        drawBox(matrices, vertexConsumers, box, red, green, blue, alpha, renderOutline);
    }

    public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, int red, int green, int blue, int alpha, boolean renderOutline) {
        DebugRenderer.drawBox(matrices, vertexConsumers, box, (float) red / 255, (float) green / 255, (float) blue / 255, (float) alpha / 255);
        if(renderOutline) drawOutline(matrices, vertexConsumers, box, 0.07f, red, green, blue);
    }

    public static void drawEntityBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d targetPos, double size, Entity entity, int red, int green, int blue, int alpha) {
        Vec3d offsetEntityPos = entity.getPos().add(0, 0, 0);
        DebugRenderer.drawBox(matrices, vertexConsumers, Box.of(targetPos, size, size, size).offset(-offsetEntityPos.x, -offsetEntityPos.y, -offsetEntityPos.z), (float) red / 255, (float) green / 255, (float) blue / 255, (float) alpha / 255);
    }

    public static void drawLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d camera, Vec3d startPos, Vec3d targetPos, int red, int green, int blue, int alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        vertexConsumer.vertex(matrices.peek(), (float) (startPos.x - camera.x), (float) (startPos.y - camera.y), (float) (startPos.z - camera.z)).color(getArgb(alpha, red, green, blue));
        vertexConsumer.vertex(matrices.peek(), (float) (targetPos.x - camera.x), (float) (targetPos.y - camera.y), (float) (targetPos.z - camera.z)).color(getArgb(alpha, red, green, blue));
    }

    public static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getRgb(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    public static void drawOutline(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float thickness, int red, int green, int blue) {
        for (Box edge : getEdges(box, thickness)) {
            DebugRenderer.drawBox(matrices, vertexConsumers, edge, (float) red / 255, (float) green / 255, (float) blue / 255, (float) 255 / 255);
        }
    }

    private static List<Box> getEdges(Box box, double thickness) {
        List<Box> edges = new ArrayList<>();
        edges.add(Box.of(new Vec3d((box.minX + box.maxX) / 2, box.minY, box.minZ), box.minX - box.maxX - thickness, thickness, thickness));
        edges.add(Box.of(new Vec3d((box.minX + box.maxX) / 2, box.maxY, box.minZ), box.minX - box.maxX - thickness, thickness, thickness));
        edges.add(Box.of(new Vec3d((box.minX + box.maxX) / 2, box.minY, box.maxZ), box.minX - box.maxX - thickness, thickness, thickness));
        edges.add(Box.of(new Vec3d((box.minX + box.maxX) / 2, box.maxY, box.maxZ), box.minX - box.maxX - thickness, thickness, thickness));

        edges.add(Box.of(new Vec3d(box.minX, (box.minY + box.maxY) / 2, box.minZ), thickness, box.minY - box.maxY - thickness, thickness));
        edges.add(Box.of(new Vec3d(box.maxX, (box.minY + box.maxY) / 2, box.minZ), thickness, box.minY - box.maxY - thickness, thickness));
        edges.add(Box.of(new Vec3d(box.minX, (box.minY + box.maxY) / 2, box.maxZ), thickness, box.minY - box.maxY - thickness, thickness));
        edges.add(Box.of(new Vec3d(box.maxX, (box.minY + box.maxY) / 2, box.maxZ), thickness, box.minY - box.maxY - thickness, thickness));

        edges.add(Box.of(new Vec3d(box.minX, box.minY, (box.minZ + box.maxZ) / 2), thickness, thickness, box.minZ - box.maxZ - thickness));
        edges.add(Box.of(new Vec3d(box.maxX, box.minY, (box.minZ + box.maxZ) / 2), thickness, thickness, box.minZ - box.maxZ - thickness));
        edges.add(Box.of(new Vec3d(box.minX, box.maxY, (box.minZ + box.maxZ) / 2), thickness, thickness, box.minZ - box.maxZ - thickness));
        edges.add(Box.of(new Vec3d(box.maxX, box.maxY, (box.minZ + box.maxZ) / 2), thickness, thickness, box.minZ - box.maxZ - thickness));

        return edges;
    }

}
