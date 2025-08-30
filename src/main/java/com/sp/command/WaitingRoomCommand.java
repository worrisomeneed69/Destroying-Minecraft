package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.sp.DestroyingMinecraft;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;

public class WaitingRoomCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("setinwaitingroom")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(CommandManager.argument("setinwaitingroom", BoolArgumentType.bool())
                                .executes(context -> execute(
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        BoolArgumentType.getBool(context, "setinwaitingroom")
                                    )
                                )
                            )
                        )
        );
    }

    private static int execute(Collection<ServerPlayerEntity> targets, boolean setInWaitingRoom) {
        if (targets.isEmpty()) {
            return -1;
        }

        for (ServerPlayerEntity player : targets) {
            DestroyingMinecraft.sendWaitingRoomPacket(player, setInWaitingRoom);
        }

        return 1;
    }

}
