package com.sp.item.custom;

import com.sp.component.ModDataComponentTypes;
import com.sp.sounds.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class WalkieTalkie extends Item {

    public WalkieTalkie(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        itemStack.set(ModDataComponentTypes.WALKIE_TALKIE_ON, true);
        world.playSoundFromEntity(user, user, ModSounds.WALKIE_TALKIE, SoundCategory.PLAYERS, 0.4F, 1.0F);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.set(ModDataComponentTypes.WALKIE_TALKIE_ON, false);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
}
