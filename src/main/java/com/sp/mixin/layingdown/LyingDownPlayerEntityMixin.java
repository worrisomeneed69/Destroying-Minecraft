package com.sp.mixin.layingdown;

import com.sp.block.custom.ChairBlock;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class LyingDownPlayerEntityMixin extends LivingEntity implements LayingDownPlayerEntity {
    protected LyingDownPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> LAYING_DOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Optional<BlockPos>> LAYING_DOWN_POS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);


    @Shadow protected abstract boolean shouldDismount();


    @Override
    public boolean isLayingDown() {
        return this.dataTracker.get(LAYING_DOWN) && this.getLayingDownPos().isPresent();
    }

    @Override
    public void setLayingDown(boolean layingDown) {
        this.dataTracker.set(LAYING_DOWN, layingDown);
    }

    @Override
    public void setLayingDownPos(BlockPos layingDownPos) {
        this.dataTracker.set(LAYING_DOWN_POS, layingDownPos != null ? Optional.of(layingDownPos) : Optional.empty());
    }

    @Override
    public Optional<BlockPos> getLayingDownPos() {
        return this.dataTracker.get(LAYING_DOWN_POS);
    }


    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addLayingDown(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(LAYING_DOWN, false);
        builder.add(LAYING_DOWN_POS, Optional.empty());
    }


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;updatePose()V"))
    private void stopLayingDown(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            if (this.isLayingDown() && this.shouldDismount()) {
                this.setPose(EntityPose.STANDING);

                BlockState blockState = this.getWorld().getBlockState(this.getLayingDownPos().get());

                if (blockState.getBlock() instanceof ChairBlock) {
                    this.getWorld().setBlockState(this.getLayingDownPos().get(), blockState.with(ChairBlock.OCCUPIED, false));
                }

                this.setLayingDown(false);
                this.setLayingDownPos(null);
            }
        }

        if (this.isLayingDown()) {
            Direction layingDownDirection = this.getLayingDownDirection();
            if (layingDownDirection != null) {
                layingDownDirection = layingDownDirection.getOpposite();

                Vec3d offset = new Vec3d(
                        layingDownDirection.getOffsetX(),
                        layingDownDirection.getOffsetY(),
                        layingDownDirection.getOffsetZ()
                ).multiply(0.25).add(0, 0.1, 0);

                switch (layingDownDirection) {
                    case NORTH -> this.clampPassengerYaw(180);
                    case SOUTH -> this.clampPassengerYaw(0);
                    case EAST -> this.clampPassengerYaw(-90);
                    case WEST -> this.clampPassengerYaw(90);
                }

                this.setPosition(this.getLayingDownPos().get().toCenterPos().add(offset));
                this.setVelocity(0, 0, 0);
            }
        }
    }

    @Override
    public Direction getLayingDownDirection() {
        BlockPos layingDownPos = this.getLayingDownPos().orElse(null);
        return layingDownPos != null ? ChairBlock.getDirection(getWorld(), layingDownPos) : null;
    }

    @Unique
    protected void clampPassengerYaw(float directionYaw) {
//        this.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(this.getYaw() - directionYaw);
        float g = MathHelper.clamp(f, -90.0F, 90.0F);
        this.prevYaw += g - f;
        this.setYaw(this.getYaw() + g - f);
        this.setHeadYaw(this.getYaw());
    }
}
