package com.sp.block;

import com.sp.DestroyingMinecraft;
import com.sp.block.custom.ChairBlock;
import com.sp.block.custom.LimboSquareBlock;
import com.sp.block.custom.PhysicsDoorBlock;
import com.sp.block.custom.VoidBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static final Block WHITE_VOID_BLOCK = registerBlock("white_void_block",
            new VoidBlock(AbstractBlock.Settings.copy(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles().luminance(value -> 15)));

    public static final Block PHYSICS_DOOR_BLOCK = registerBlock("physics_door_block",
            new PhysicsDoorBlock(AbstractBlock.Settings.copy(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block CHAIR_BLOCK = registerBlock("chair_block",
            new ChairBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_STAIRS).solid().noBlockBreakParticles()));

    public static final Block LIMBO_SQUARE_BLOCK = registerBlock("limbo_square_block",
            new LimboSquareBlock(AbstractBlock.Settings.copy(Blocks.GLASS).solid().noBlockBreakParticles()));



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
