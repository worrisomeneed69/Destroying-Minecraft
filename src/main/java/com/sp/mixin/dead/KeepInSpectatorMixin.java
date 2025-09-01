package com.sp.mixin.dead;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class KeepInSpectatorMixin {

    @WrapMethod(method = "changeGameMode")
    private boolean dontChangeGameModeAfterDeath(GameMode gameMode, Operation<Boolean> original) {
        PlayerComponent component = InitializeComponents.PLAYERS.get((ServerPlayerEntity) (Object) this);

        if (component.hasDied() && gameMode != GameMode.SPECTATOR) {
            return false;
        }
        return original.call(gameMode);
    }

}
