package com.sp.entity.client.renderer;

import com.sp.DestroyingMinecraftClient;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PhysicsBlockComponent;
import com.sp.collision.BlockOBB;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.util.MathUtil;
import com.sp.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

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



    @Override
    public void render(BlockPhysicsEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (DestroyingMinecraftClient.shouldRenderDebug) {
            renderDebug(entity, matrices, vertexConsumers);
        }

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

        PhysicsBlockComponent component = InitializeComponents.PHYSICS_BLOCK.get(entity);

        World world = entity.getWorld();
        if (world == null) {
            return;
        }

        for (BlockPhysicsEntity.BlockData blockData : entity.getBlocks()) {
            matrices.push();

            matrices.multiply(component.getRotation());
            matrices.translate(-.5, -.5, -.5);
            matrices.translate(blockData.offset.getX(), blockData.offset.getY(), blockData.offset.getZ());

            BlockState blockState = blockData.blockState;
            BlockPos entityPos = entity.getBlockPos();
            Vec3d offsetPos = blockData.offset;
            Vector3f rotatedPos = component.getRotation().transform(offsetPos.toVector3f());


//            System.out.println(entity.getPos());

            blockModelRenderer.getModelRenderer().render(
                    world,
                    blockModelRenderer.getModel(blockState),
                    blockState,
                    entityPos.add(BlockPos.ofFloored(MathUtil.toVec3d(rotatedPos))),
                    matrices,
                    vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    Random.create(),
                    1,
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderDebug(BlockPhysicsEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        List<Vec3d> aabbCorners = BlockOBB.getAABBCorners(MinecraftClient.getInstance().player.getBoundingBox());

        //Testing out making the debug render smoother
        for (int i = 0; i < aabbCorners.size(); i++) {
            Vec3d aabbCorner = aabbCorners.get(i);

            //RenderUtil.drawEntityBox(matrices, vertexConsumers, aabbCorner.add(0.5, 0.5, 0.5), 0.2, entity, 0, 0, 255, 255);
        }

        for (BlockPhysicsEntity.BlockData block : entity.getBlocks()) {
            BlockOBB obb = new BlockOBB(entity.component.getRotation(), block);

//            List<Vec3d> obbCorners = obb.getGlobalCorners(entity);
//
//            Vec3d globalPos = MathUtil.toVec3d(obb.rotation.transform(MathUtil.toVector3d(Vec3d.of(obb.blockData.offset)))).add(entity.getPos());
//
//            for (Vec3d normalAxi : obb.getNormalAxis()) {
//                Vec3d sideStart = globalPos.add(normalAxi.multiply(0.5));
//
//                RenderUtil.drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideStart.add(normalAxi), 0, 0, 255, 255);
//            }

            for (Vec3d globalCorner : obb.getGlobalCorners(entity)) {
                boolean collides = obb.collidesWith(MinecraftClient.getInstance().player.getBoundingBox(), entity);
                RenderUtil.drawEntityBox(matrices, vertexConsumers, globalCorner.add(0.5, 0.5, 0.5), 0.2, entity, collides ? 255 : 0,  collides ? 0 : 255, 0, 255);
            }

        /*
            List<Vec3d> allAxis = obb.getAABBNormalAxis();
            allAxis.addAll(obb.getNormalAxis());
//            allAxis.addAll(obb.getCrossProductAxis(obb.getNormalAxis(), obb.getAABBNormalAxis()));

            for (Vec3d axis : allAxis) {
                Vec3d sideStart = globalPos.add(axis.multiply(2));
                Vec3d sideEnd = globalPos.add(axis.multiply(-2));

//                RenderUtil.drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 255, 255, 0, 255);

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
                    // RenderUtil.drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 0, 255, 0, 255);
                } else {
                    // RenderUtil.drawLine(matrices, vertexConsumers, entity.getPos(), sideStart, sideEnd, 255, 0, 0, 255);
                }
            }

         */
        }
    }
}
