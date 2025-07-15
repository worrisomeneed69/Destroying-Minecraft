package com.sp.entity.custom;

import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MeteorEntity extends PersistentProjectileEntity {

    public MeteorEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);

    }

    @Override
    public void tick() {
        super.tick();

        if(!this.inGround && !this.isOnGround()) {

            this.setVelocity(new Vec3d(-1, -1, 0));
            this.move(MovementType.SELF, this.getVelocity());
        }

        if (!this.getWorld().isClient) {
            if (this.age > 200) {
                this.onBlockHit(null);
            }

        }

    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 100.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        PointSBE explosion = new PointSBE(5, 0.5f, this.getPos());
        explosion.beginExplosion();
        discard();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }
}
