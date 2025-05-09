package com.sp.cca;

import com.sp.DestroyingMinecraft;
import com.sp.cca.custom.PhysicsBlockComponent;
import com.sp.entity.custom.BlockPhysicsEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class InitializeComponents implements EntityComponentInitializer {
    public static final ComponentKey<PhysicsBlockComponent> SPINNING_BLOCK = ComponentRegistry.getOrCreate(DestroyingMinecraft.idOf("spin_block"), PhysicsBlockComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(BlockPhysicsEntity.class, SPINNING_BLOCK, PhysicsBlockComponent::new);
    }
}
