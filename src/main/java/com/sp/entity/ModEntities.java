package com.sp.entity;

import com.sp.DestroyingMinecraft;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BlockPhysicsEntity> BLOCK_PHYSICS_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DestroyingMinecraft.MOD_ID, "blockphysicsentity"),
            EntityType.Builder.create(BlockPhysicsEntity::new, SpawnGroup.MISC).dimensions(1f, 1f).build()
    );

    public static void init() {

    }
}
