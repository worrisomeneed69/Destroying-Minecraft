package com.sp.collision;

import com.sp.entity.client.renderer.BlockPhysicsEntityRenderer;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockOBB {
    public Quaternionf rotation;
    public BlockPhysicsEntity.BlockData blockData;
    public Vec3d halfSize = new Vec3d(.5, .5, .5);

    public BlockOBB(Quaternionf rotation, BlockPhysicsEntity.BlockData blockData) {
        this.rotation = rotation;
        this.blockData = blockData;
    }

    public Vec3d[] getLocalCorners() {
        Vec3d[] corners = {
                new Vec3d(halfSize.x, halfSize.y, halfSize.z),
                new Vec3d(-halfSize.x, halfSize.y, halfSize.z),
                new Vec3d(-halfSize.x, halfSize.y, -halfSize.z),
                new Vec3d(halfSize.x, halfSize.y, -halfSize.z),

                new Vec3d(halfSize.x, -halfSize.y, halfSize.z),
                new Vec3d(-halfSize.x, -halfSize.y, halfSize.z),
                new Vec3d(-halfSize.x, -halfSize.y, -halfSize.z),
                new Vec3d(halfSize.x, -halfSize.y, -halfSize.z)
        };

        for (int i = 0; i < corners.length; i++) {
            Vec3d corner = corners[i];

            Vec3d newCornerPos = MathUtil.toVec3d(rotation.transform(MathUtil.toVector3d(corner.add(new Vec3d(blockData.offset.getX(), blockData.offset.getY(), blockData.offset.getZ())))));

            corners[i] = newCornerPos.add(-.5, -.5, -.5);
        }

        return corners;
    }

    public List<Vec3d> getGlobalCorners(Entity entity) {
        return Arrays.stream(this.getLocalCorners()).map(corner -> corner.add(entity.getPos())).collect(Collectors.toList());
    }

    public static List<Vec3d> getAABBCorners(Box aabb) {
        List<Vec3d> corners = new ArrayList<>();

        corners.add(new Vec3d(aabb.minX - .5, aabb.minY - .5, aabb.minZ - .5));
        corners.add(new Vec3d(aabb.maxX - .5, aabb.minY - .5, aabb.minZ - .5));
        corners.add(new Vec3d(aabb.minX - .5, aabb.maxY - .5, aabb.minZ - .5));
        corners.add(new Vec3d(aabb.maxX - .5, aabb.maxY - .5, aabb.minZ - .5));
        corners.add(new Vec3d(aabb.minX - .5, aabb.minY - .5, aabb.maxZ - .5));
        corners.add(new Vec3d(aabb.maxX - .5, aabb.minY - .5, aabb.maxZ - .5));
        corners.add(new Vec3d(aabb.minX - .5, aabb.maxY - .5, aabb.maxZ - .5));
        corners.add(new Vec3d(aabb.maxX - .5, aabb.maxY - .5, aabb.maxZ - .5));

        return corners;
    }

    public List<Vec3d> getNormalAxis() {
        Vec3d[] axis = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 1, 0),
                new Vec3d(0, 0, 1)
        };

        for (int i = 0; i < axis.length; i++) {
            Vec3d axisVec = axis[i];
            axis[i] = MathUtil.toVec3d(this.rotation.transform(axisVec.toVector3f())).normalize();
        }

        return Arrays.asList(axis);
    }

    public static List<Vec3d> getAABBNormalAxis() {
        List<Vec3d> axis = new ArrayList<>();

        axis.add(new Vec3d(1, 0, 0));
        axis.add(new Vec3d(0, 1, 0));
        axis.add(new Vec3d(0, 0, 1));

        return axis;
    }

    /*
    public RayCollisionResult raycast(Vec3d origin, Vec3d direction, Vec3d thisPos) {
        Vec3d localOrigin = origin.subtract(thisPos);

        Vec3d deRotatedOrigin = BlockPhysicsEntity.toVec3(this.rotation.transform(BlockPhysicsEntity.toVector3d(localOrigin)));

        Vec3d deRotatedDirection = BlockPhysicsEntity.toVec3(this.rotation.transform(BlockPhysicsEntity.toVector3d(direction)));

        double tMin = Float.NEGATIVE_INFINITY, tMax = Float.POSITIVE_INFINITY;

        Vec3d minBounds = new Vec3d(-0.5, -0.5, -0.5);
        Vec3d maxBounds = new Vec3d(0.5,  0.5,  0.5);

        if (Math.abs(deRotatedDirection.getX()) < 1e-6) {
            if (deRotatedOrigin.getX() < minBounds.getX() || deRotatedOrigin.getX() > maxBounds.getX()) return new RayCollisionResult(0, 0, false);
        } else {
            double t1 = (minBounds.getX() - deRotatedOrigin.getX()) / deRotatedDirection.getX();
            double t2 = (maxBounds.getX() - deRotatedOrigin.getX()) / deRotatedDirection.getX();

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) return new RayCollisionResult(0, 0, false);
        }

        if (Math.abs(deRotatedDirection.getY()) < 1e-6) {
            if (deRotatedOrigin.getY() < minBounds.getY() || deRotatedOrigin.getY() > maxBounds.getY()) return new RayCollisionResult(0, 0, false);
        } else {
            double t1 = (minBounds.getY() - deRotatedOrigin.getY()) / deRotatedDirection.getY();
            double t2 = (maxBounds.getY() - deRotatedOrigin.getY()) / deRotatedDirection.getY();

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) return new RayCollisionResult(0, 0, false);
        }

        if (Math.abs(deRotatedDirection.getZ()) < 1e-6) {
            if (deRotatedOrigin.getZ() < minBounds.getZ() || deRotatedOrigin.getZ() > maxBounds.getZ()) return new RayCollisionResult(0, 0, false);
        } else {
            double t1 = (minBounds.getZ() - deRotatedOrigin.getZ()) / deRotatedDirection.getZ();
            double t2 = (maxBounds.getZ() - deRotatedOrigin.getZ()) / deRotatedDirection.getZ();

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) return new RayCollisionResult(0, 0, false);
        }


    }
     */

    public record RayCollisionResult(double tMin, double tMax, boolean collide) {}

    public List<Vec3d> getCrossProductAxis(List<Vec3d> obbAxis, List<Vec3d> aabbAxis) {
        List<Vec3d> crossProductAxis = new ArrayList<>();

        for (Vec3d obb : obbAxis) {
            for (Vec3d aabb : aabbAxis) {
                Vec3d crossProduct = obb.crossProduct(aabb);

                Vec3d norm = crossProduct.normalize();
                crossProductAxis.add(norm);
            }
        }

        return crossProductAxis;
    }


    public List<CollisionData> getAllCollisionAxisWith(Box aabb, Entity entity) {
        List<CollisionData> collisions = new ArrayList<>();

        List<Vec3d> obbCorners = this.getGlobalCorners(entity);
        List<Vec3d> aabbCorners = getAABBCorners(aabb);

        List<Vec3d> allAxis = getAABBNormalAxis();
        allAxis.addAll(this.getNormalAxis());
        allAxis.addAll(this.getCrossProductAxis(this.getNormalAxis(), getAABBNormalAxis()));

        for (Vec3d axis : allAxis) {
            // Skip zero vectors that might result from cross products
            if (axis.lengthSquared() < 1e-10) {
                continue;
            }

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


            double overlap = Math.min(obbMax - aabbMin, aabbMax - obbMin);

            if (overlap > 1) {
                collisions.add(new CollisionData(overlap, axis, true));
            }
        }

        return collisions;
    }

    public boolean collidesWith(Box aabb, Entity entity) {
        List<Vec3d> obbCorners = this.getGlobalCorners(entity);
        List<Vec3d> aabbCorners = getAABBCorners(aabb);

        List<Vec3d> allAxis = getAABBNormalAxis();
        allAxis.addAll(this.getNormalAxis());
        allAxis.addAll(this.getCrossProductAxis(this.getNormalAxis(), getAABBNormalAxis()));

        for (Vec3d axis : allAxis) {
            // Skip zero vectors that might result from cross products
            if (axis.lengthSquared() < 1e-10) {
                continue;
            }

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

            // Check for separation
            if (obbMax < aabbMin || obbMin > aabbMax) {
        return false;
    }
        }

        return true;
    }


    public CollisionData getYAxisCollisionWith(Box aabb, Entity entity) {
        List<Vec3d> obbCorners = this.getGlobalCorners(entity);
        List<Vec3d> aabbCorners = getAABBCorners(aabb);

        double minOverlap = Double.MAX_VALUE;
        Vec3d minAxis = null;
        boolean collides = true;

        Vec3d axis = new Vec3d(0, 1, 0);

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

        // Check for separation but don't exit early
        if (obbMax < aabbMin || obbMin > aabbMax) {
            collides = false;
        }

        double overlap = Math.min(obbMax - aabbMin, aabbMax - obbMin);

        if (overlap < minOverlap) {
            minOverlap = overlap;
            minAxis = axis;
        }

        return new CollisionData(minOverlap, minAxis, collides);
    }

    public CollisionData getMinCollisionWith(Box aabb, Entity entity) {
        List<Vec3d> obbCorners = this.getGlobalCorners(entity);
        List<Vec3d> aabbCorners = getAABBCorners(aabb);

        List<Vec3d> allAxis = getAABBNormalAxis();
        allAxis.addAll(this.getNormalAxis());
//        allAxis.addAll(this.getCrossProductAxis(this.getNormalAxis(), getAABBNormalAxis()));

        double minOverlap = Double.MAX_VALUE;
        Vec3d minAxis = null;
        boolean collides = true;

        for (Vec3d axis : allAxis) {
            // Skip zero vectors that might result from cross products
            if (axis.lengthSquared() < 1e-10) {
                continue;
            }

            double obbMin = Double.MAX_VALUE, obbMax = Double.MIN_VALUE;
            double aabbMin = Double.MAX_VALUE, aabbMax = Double.MIN_VALUE;

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

            // Check for separation but don't exit early
            if (obbMax < aabbMin || obbMin > aabbMax) {
                collides = false;
            }

            double overlap = Math.min(obbMax - aabbMin, aabbMax - obbMin);

            if (overlap < minOverlap) {
                minOverlap = overlap;
                minAxis = axis;
            }
        }

        return new CollisionData(minOverlap, minAxis, collides);
    }

    public record CollisionData(double overLapp, Vec3d axis, boolean collides) {}
}
