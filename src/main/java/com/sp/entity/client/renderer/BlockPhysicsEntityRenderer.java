package com.sp.entity.client.renderer;

import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.PhysicsBlockComponent;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3d;

public class BlockPhysicsEntityRenderer extends EntityRenderer<BlockPhysicsEntity> {
    private final BlockRenderManager blockModelRenderer;

    public BlockPhysicsEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockModelRenderer = ctx.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(BlockPhysicsEntity entity) {
        return null;
    }

    public static Vector3d toVector3d(Vec3d vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vec3d toVec3d(Vector3d vec) {
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    @Override
    public void render(BlockPhysicsEntity entity, float fyaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, fyaw, tickDelta, matrices, vertexConsumers, light);

        /*
        List<Vec3d> aabbCorners = BlockOBB.getAABBCorners(MinecraftClient.getInstance().player.getBoundingBox());

        for (Vec3d aabbCorner : aabbCorners) {
            drawBox(matrices, vertexConsumers, aabbCorner, entity, 0, 0, 255, 255);
        }

        for (BlockPhysicsEntity.BlockData block : entity.blocks) {
            BlockOBB obb = new BlockOBB(entity.rotation, block);

            List<Vec3d> obbCorners = obb.getGlobalCorners(entity);

            Vec3d globalPos = toVec3d(obb.rotation.transform(toVector3d(obb.blockData.offset))).add(entity.getPos());

            for (Vec3d normalAxi : obb.getNormalAxis()) {
                Vec3d sideStart = globalPos.add(normalAxi.multiply(0.5));

                drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideStart.add(normalAxi), 0, 0, 255, 255);
            }

            for (Vec3d globalCorner : obb.getGlobalCorners(entity)) {
                drawBox(matrices, vertexConsumers, globalCorner, entity, 255, 0, 0, 255);
            }

            List<Vec3d> allAxis = obb.getAABBNormalAxis();
            allAxis.addAll(obb.getNormalAxis());
            allAxis.addAll(obb.getCrossProductAxis(obb.getNormalAxis(), obb.getAABBNormalAxis()));

            for (Vec3d axis : allAxis) {
                Vec3d sideStart = globalPos.add(axis.multiply(2));
                Vec3d sideEnd = globalPos.add(axis.multiply(-2));

                drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 255, 255, 0, 255);

                double obbMin = Double.MAX_VALUE, obbMax = -Double.MAX_VALUE;
                double aabbMin = Double.MAX_VALUE, aabbMax = -Double.MAX_VALUE;

                for (Vec3d corner : obbCorners) {
                    double projection = axis.dotProduct(corner);
                    obbMin = Math.min(obbMin, projection);
                    obbMax = Math.max(obbMax, projection);
                }

                for (Vec3d aabbCorner : aabbCorners) {
                    double projection = axis.dotProduct(aabbCorner);
                    aabbMin = Math.min(aabbMin, projection);
                    aabbMax = Math.max(aabbMax, projection);
                }

                if (obbMax < aabbMin || obbMin > aabbMax) {
                    drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 0, 255, 0, 255);
                } else {
                    drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 255, 0, 0, 255);
                }
            }
        }

         */

        /*
        List<Vec3d> aabbCorners = BlockOBB.getAABBCorners(MinecraftClient.getInstance().player.getBoundingBox());
        for (BlockPhysicsEntity.BlockData block : entity.blocks) {


            BlockOBB obb = new BlockOBB(entity.rotation, block);

            List<Vec3d> obbCorners = obb.getGlobalCorners(entity);
            Vec3d globalPos = toVec3d(obb.rotation.transform(toVector3d(obb.blockData.offset))).add(entity.getPos());

            List<Vec3d> allAxis = obb.getAABBNormalAxis();
            allAxis.addAll(obb.getNormalAxis());
            allAxis.addAll(obb.getCrossProductAxis(obb.getNormalAxis(), obb.getAABBNormalAxis()));

            double minOverlap = Double.MAX_VALUE;
            Vec3d minAxis = null;

            for (Vec3d axis : allAxis) {
                Vec3d sideStart = globalPos.add(axis.multiply(2));
                Vec3d sideEnd = globalPos.add(axis.multiply(-2));

                double obbMin = Double.MAX_VALUE, obbMax = -Double.MAX_VALUE;
                double aabbMin = Double.MAX_VALUE, aabbMax = -Double.MAX_VALUE;

                for (Vec3d corner : obbCorners) {
                    double projection = axis.dotProduct(corner);
                    obbMin = Math.min(obbMin, projection);
                    obbMax = Math.max(obbMax, projection);
                }

                for (Vec3d aabbCorner : aabbCorners) {
                    double projection = axis.dotProduct(aabbCorner);
                    aabbMin = Math.min(aabbMin, projection);
                    aabbMax = Math.max(aabbMax, projection);
                }

                if (obbMax < aabbMin || obbMin > aabbMax) {
                    new BlockOBB.CollisionData(0, null, false);
                    drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 0, 255, 0, 255);
                } else {
                    drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 255, 0, 0, 255);
                }

                // Calculate overlap along this axis
                double overlap = Math.min(obbMax - aabbMin, aabbMax - obbMin);

                if (overlap < minOverlap) {
                    minOverlap = overlap;
                    minAxis = axis;
                }
            }


            BlockOBB.CollisionData collisionData = new BlockOBB.CollisionData(minOverlap, minAxis, true);

            Vec3d newPos = MinecraftClient.getInstance().player.getPos().add(collisionData.axis().multiply(collisionData.overLapp()));

            drawLine(matrices, vertexConsumers, entity.getPos(), MinecraftClient.getInstance().player.getPos(), newPos, 0, 0, 255, 255);
        }
         */

        // FIXME: Colors and light are horribly of.

        PhysicsBlockComponent component = InitializeComponents.PHYSICS_BLOCK.get(entity);

        World world = entity.getWorld();
        if (world == null) {
            return;
        }

        for (BlockPhysicsEntity.BlockData blockData : component.getBlocks()) {
            matrices.push();

            matrices.multiply(component.getRotation());
            matrices.translate(-.5, -.5, -.5);
            matrices.translate(blockData.offset.getX(), blockData.offset.getY(), blockData.offset.getZ());

            BlockState blockState = blockData.blockState;
            BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

            blockModelRenderer.getModelRenderer().render(
                    world,
                    blockModelRenderer.getModel(blockState),
                    blockState,
                    blockPos,
                    matrices,
                    vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    true,
                    Random.create(),
                    1,
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }
    }


    static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d targetPos, Entity entity, int red, int green, int blue, int alpha) {
        Vec3d offsetEntityPos = entity.getPos().add(0, 0, 0);

        DebugRenderer.drawBox(matrices, vertexConsumers, Box.from(targetPos).contract(0.4).offset(-offsetEntityPos.x, -offsetEntityPos.y, -offsetEntityPos.z), (float) red / 255, (float) green / 255, (float) blue / 255, (float) alpha / 255);
    }

    static void drawLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d camera, Vec3d startPos, Vec3d targetPos, int red, int green, int blue, int alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        vertexConsumer.vertex(matrices.peek(), (float) (startPos.x - camera.x), (float) (startPos.y - camera.y), (float) (startPos.z - camera.z)).color(getArgb(alpha, red, green, blue));
        vertexConsumer.vertex(matrices.peek(), (float) (targetPos.x - camera.x), (float) (targetPos.y - camera.y), (float) (targetPos.z - camera.z)).color(getArgb(alpha, red, green, blue));
    }

    static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}
