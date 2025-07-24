package com.sp.item;

import com.sp.DestroyingMinecraft;
import com.sp.component.ModDataComponentTypes;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public class ModModelPredicates {

    public static void registerModelPredicates() {
        ModelPredicateProviderRegistry.register(ModItems.WALKIE_TALKIE_ITEM, DestroyingMinecraft.idOf("on"),
                (stack, world, entity, seed) ->
                        Boolean.TRUE.equals(stack.get(ModDataComponentTypes.WALKIE_TALKIE_ON)) ? 1.0f : 0.0f
        );
    }
}
