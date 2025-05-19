package com.sp.entity;

import com.sp.DestroyingMinecraft;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.entity.custom.MeteorEntity;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SpinningBlockEntity> SPINNING_BLOCK = Registry.register(Registries.ENTITY_TYPE,
            DestroyingMinecraft.idOf("spinning_block"),
            EntityType.Builder.create(SpinningBlockEntity::new, SpawnGroup.MISC)
                    .dimensions(1.0F, 1.0F)
                    .build()
    );

    public static final EntityType<BlockPhysicsEntity> BLOCK_PHYSICS_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            DestroyingMinecraft.idOf("blockphysicsentity"),
            EntityType.Builder.create(BlockPhysicsEntity::new, SpawnGroup.MISC)
                    .dimensions(1f, 1f)
                    .build()
    );

    public static final EntityType<MeteorEntity> METEOR_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            DestroyingMinecraft.idOf("meteor"),
            EntityType.Builder.create(MeteorEntity::new, SpawnGroup.MISC)
                    .dimensions(1.0F, 1.0F)
                    .build()
    );

    public static void registerEntities(){
        DestroyingMinecraft.LOGGER.info("Registering entities for " + DestroyingMinecraft.MOD_ID);
    }
}
