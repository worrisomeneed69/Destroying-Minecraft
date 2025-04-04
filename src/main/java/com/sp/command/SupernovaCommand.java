package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sp.render.supernova.SupernovaRenderer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class SupernovaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("supernova")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> execute()
                        )
        );
    }

    private static int execute(){
        SupernovaRenderer.resetSupernovaTimer();
        return 1;
    }
}
