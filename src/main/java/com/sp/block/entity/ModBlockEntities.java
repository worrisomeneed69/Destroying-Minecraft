package com.sp.block.entity;

import com.sp.DestroyingMinecraft;
import com.sp.block.ModBlocks;
import com.sp.block.entity.custom.LimboSquareBlockEntity;
import com.sp.block.entity.custom.PhysicsDoorBlockEntity;
import com.sp.block.entity.custom.voidblock.GlitchedVoidBlockEntity;
import com.sp.block.entity.custom.voidblock.VoidBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {

    public static final BlockEntityType<PhysicsDoorBlockEntity> PHYSICS_DOOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, DestroyingMinecraft.idOf("physics_door_be"),
                    BlockEntityType.Builder.create(PhysicsDoorBlockEntity::new, ModBlocks.PHYSICS_DOOR_BLOCK).build());

    public static final BlockEntityType<VoidBlockEntity> VOID_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, DestroyingMinecraft.idOf("void_be"),
                    BlockEntityType.Builder.create(VoidBlockEntity::new, ModBlocks.WHITE_VOID_BLOCK).build());

    public static final BlockEntityType<GlitchedVoidBlockEntity> GLITCHED_VOID_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, DestroyingMinecraft.idOf("glitched_void_be"),
                    BlockEntityType.Builder.create(GlitchedVoidBlockEntity::new, ModBlocks.GLITCHED_VOID_BLOCK).build());

    public static final BlockEntityType<LimboSquareBlockEntity> LIMBO_SQUARE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, DestroyingMinecraft.idOf("limbo_be"),
                    BlockEntityType.Builder.create(LimboSquareBlockEntity::new, ModBlocks.LIMBO_SQUARE_BLOCK).build());

    public static void registerBlockEntities() {
        DestroyingMinecraft.LOGGER.info("Registering Block Entities for " + DestroyingMinecraft.MOD_ID);
    }

}
