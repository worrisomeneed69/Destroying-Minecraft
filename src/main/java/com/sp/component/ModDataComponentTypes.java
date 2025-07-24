package com.sp.component;

import com.mojang.serialization.Codec;
import com.sp.DestroyingMinecraft;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<Boolean> WALKIE_TALKIE_ON = register("walkie_talkie_on",
            builder -> builder.codec(Codec.BOOL));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, DestroyingMinecraft.idOf(name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {

    }
}
