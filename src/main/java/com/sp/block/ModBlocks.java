package com.sp.block;

import com.sp.DestroyingMinecraft;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block WhiteVoidBlock = registerBlock("white_void_block",
            new Block(AbstractBlock.Settings.copy(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles().luminance(value -> 15)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, DestroyingMinecraft.idOf(name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, DestroyingMinecraft.idOf(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void init() {
    }

}
