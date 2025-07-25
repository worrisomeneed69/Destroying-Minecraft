package com.sp.mixin.multipledeathvariants;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.sp.entity.ModDamageSources;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {

    @Shadow @Final private LivingEntity entity;

    @WrapOperation(method = "getDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getDeathMessage(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/text/Text;"))
    private Text isPlayZoneDeath(DamageSource instance, LivingEntity killed, Operation<Text> original, @Local DamageSource damageSource) {
        if (instance.getType().deathMessageType() == ModDamageSources.PLAY_ZONE_TYPE) {
            int randomInt = killed.getRandom().nextBetween(1, 7);
            return Text.translatable("death.attack." + damageSource.getName() + randomInt, this.entity.getDisplayName());
        } else if (instance.getType().deathMessageType() == ModDamageSources.CRACKS_TYPE) {
            int randomInt = killed.getRandom().nextBetween(1, 4);
            return Text.translatable("death.attack." + damageSource.getName() + randomInt, this.entity.getDisplayName());
        }
        return original.call(instance, killed);
    }

}
