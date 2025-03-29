package com.sp.item;

import com.sp.DestroyingMinecraft;
import com.sp.item.custom.CameraShakeStick;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item CAMERA_SHAKE_STICK = registerItem("camera_shake_stick",
            new CameraShakeStick(new Item.Settings()));


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, DestroyingMinecraft.idOf(name), item);
    }

    public static void registerModItems() {
        DestroyingMinecraft.LOGGER.info("Registering Mod Items for " + DestroyingMinecraft.MOD_ID);
    }

}
