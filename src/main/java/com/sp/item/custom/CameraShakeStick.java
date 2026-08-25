package com.sp.item.custom;

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
        if(world.isClient) {
            ClientShakeHandler.onUse();
        }
        return super.use(world, user, hand);
    }

    private static class ClientShakeHandler {
        private static void onUse() {
            com.sp.render.camerashake.custom.CameraShakeInstance cameraShakeInstance =
                    new com.sp.render.camerashake.custom.CameraShakeInstance(1.0f, 0.0f, 100,
                            foundry.veil.api.client.util.Easing.LINEAR);

            com.sp.render.camerashake.CameraShakeManager.addCameraShake(cameraShakeInstance);
        }
    }
}
