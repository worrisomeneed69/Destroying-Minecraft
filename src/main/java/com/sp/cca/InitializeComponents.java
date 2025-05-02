package com.sp.cca;

import com.sp.DestroyingMinecraft;
import com.sp.cca.custom.SpinningBlockComponent;
import com.sp.entity.custom.SpinningBlockEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class InitializeComponents implements EntityComponentInitializer {
    public static final ComponentKey<SpinningBlockComponent> SPINNING_BLOCK = ComponentRegistry.getOrCreate(DestroyingMinecraft.idOf("spin_block"), SpinningBlockComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(SpinningBlockEntity.class, SPINNING_BLOCK, SpinningBlockComponent::new);
    }
}
