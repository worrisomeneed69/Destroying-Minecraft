package com.sp.entity.custom;

import com.sp.sounds.ModSounds;
import com.sp.util.MathUtil;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class MeteorEntity extends PersistentProjectileEntity {

    public MeteorEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);

    }

    @Override
    public void tick() {
        super.tick();


        if(!this.inGround && !this.isOnGround()) {
            this.setVelocity(new Vec3d(-1.5, -1.5, 0));
            this.move(MovementType.SELF, this.getVelocity());
        }

        if (!this.getWorld().isClient) {
            BlockHitResult hitResult = this.getWorld().raycast(
                    new RaycastContext(
                            this.getPos(),
                            this.getPos().add(0, -100, 0),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            ShapeContext.absent()
                    )
            );

            float distanceToGround = (float) hitResult.getBlockPos().toCenterPos().squaredDistanceTo(this.getPos());
            if (this.age > 200 || distanceToGround <= 4.0f) {
                this.onBlockHit(hitResult);
            }

        } else {
            if (this.age == 1) { //As soon as it spawns
                this.getWorld().playSoundFromEntity(
                        this,
                        ModSounds.METEOR_WHISTLE,
                        SoundCategory.AMBIENT,
                        10.0f,
                        MathUtil.nextBetween(0.6f, 1.2f)
                );
            }
        }

    }

    @Override
    public void onRemoved() {
//        if (this.getWorld().isClient) {
//            PointCameraShake cameraShake = new PointCameraShake(this.pos, 7, 60, Easing.LINEAR);
//            CameraShakeManager.addCameraShake();
//        }
        super.onRemoved();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.playSound(ModSounds.METEOR_IMPACT, 100.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if (!this.getWorld().isClient) {
            PointSBE explosion = new PointSBE(this.random.nextBetween(4, 7), 0.2f, this.getPos());
            explosion.beginExplosion((ServerWorld) this.getWorld());
            discard();
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.FIRE_CHARGE);
    }
}
