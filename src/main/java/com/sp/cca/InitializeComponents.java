package com.sp.cca;

import com.sp.DestroyingMinecraft;
import com.sp.cca.custom.entity.PhysicsBlockComponent;
import com.sp.cca.custom.entity.SpinningBlockComponent;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.entity.custom.SpinningBlockEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class InitializeComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<SpinningBlockComponent> SPINNING_BLOCK = ComponentRegistry.getOrCreate(DestroyingMinecraft.idOf("spin_block"), SpinningBlockComponent.class);
    public static final ComponentKey<PhysicsBlockComponent> PHYSICS_BLOCK = ComponentRegistry.getOrCreate(DestroyingMinecraft.idOf("phys_block"), PhysicsBlockComponent.class);
    public static final ComponentKey<WorldDestructionEventsComponent> EVENTS = ComponentRegistry.getOrCreate(DestroyingMinecraft.idOf("events"), WorldDestructionEventsComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(SpinningBlockEntity.class, SPINNING_BLOCK, SpinningBlockComponent::new);
        entityComponentFactoryRegistry.registerFor(BlockPhysicsEntity.class, PHYSICS_BLOCK, PhysicsBlockComponent::new);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
        worldComponentFactoryRegistry.register(EVENTS, WorldDestructionEventsComponent::new);
    }
}
