package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sp.networking.InitializePackets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class SupernovaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("supernova")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("start")
                                .executes(commandContext -> execute(commandContext, 1))
                        )
                        .then(CommandManager.literal("reset")
                                .executes(commandContext -> execute(commandContext, 0)))
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, int start){
        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new InitializePackets.SupernovaPayload(start));
        }
        return 1;
    }
}
