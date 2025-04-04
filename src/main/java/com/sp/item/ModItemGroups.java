package com.sp.item;

import com.sp.DestroyingMinecraft;
import com.sp.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup BACKROOMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            DestroyingMinecraft.idOf(DestroyingMinecraft.MOD_ID),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.destroying-minecraft"))
                    .icon(() -> new ItemStack(Blocks.TNT)).entries((displayContext, entries) -> {
                        entries.add(ModItems.CAMERA_SHAKE_STICK);
                        entries.add(ModBlocks.WhiteVoidBlock);





                    }).build());




    public static void registerItemGroups() {
        DestroyingMinecraft.LOGGER.info("Registering Item Groups");
    }
}
