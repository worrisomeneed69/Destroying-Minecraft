package com.sp.item.custom;

import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.SustainedCameraShakeInstance;
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
            SustainedCameraShakeInstance cameraShakeInstance = new SustainedCameraShakeInstance(1.0f, 100, 20, Easing.LINEAR);
            CameraShakeManager.addCameraShake(cameraShakeInstance);
        }
        return super.use(world, user, hand);
    }
}
