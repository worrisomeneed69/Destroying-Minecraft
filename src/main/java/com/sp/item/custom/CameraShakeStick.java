package com.sp.item.custom;

import com.sp.render.CameraShake;
import foundry.veil.api.client.util.Easing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CameraShakeStick extends Item {

    public CameraShakeStick(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient){
            System.out.println("USED");
            CameraShake cameraShake = new CameraShake(1.5f, 100, Easing.LINEAR);
        }
        return super.use(world, user, hand);
    }
}
