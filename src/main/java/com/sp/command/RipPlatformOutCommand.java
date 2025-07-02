package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RipPlatformOutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("makeblockphysics")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(argument("position1", BlockPosArgumentType.blockPos())
                                .then(argument("position2", BlockPosArgumentType.blockPos())
                                            .executes(context ->
                                                    createPBE(
                                                            context,
                                                            BlockPosArgumentType.getBlockPos(context, "position1"),
                                                            BlockPosArgumentType.getBlockPos(context, "position2")
                                                    )
                                            )
                                )
                        )
        );
    }

    private static int createPBE(CommandContext<ServerCommandSource> context, BlockPos position1, BlockPos position2) {
        BlockPhysicsEntity.ofBlocks(context.getSource().getWorld(), position1, position2);
        return 1;
    }
}
