package com.sp.entity.custom;

import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

            this.setVelocity(new Vec3d(0, -1, -1));
            this.move(MovementType.SELF, this.getVelocity());
        }

        if(!this.getWorld().isClient) {
            if(this.inGround || this.isOnGround()) {
//                PointSBE explosion = new PointSBE(5, 0.5f, this.getPos());
//                explosion.beginExplosion();
//                discard();
            }
        }

    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }
}
