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
                        .then(argument("position", BlockPosArgumentType.blockPos())
                                .then(argument("size", IntegerArgumentType.integer())
                                        .executes(context ->
                                                createPBE(
                                                        context,
                                                        BlockPosArgumentType.getBlockPos(context, "position"),
                                                        IntegerArgumentType.getInteger(context, "size")
                                                )
                                        )
                                )
                        )
        );
    }

    private static int createPBE(CommandContext<ServerCommandSource> context, BlockPos position, int size) {
        BlockPos first = new BlockPos(position.getX() + size, position.getY() + size, position.getZ() + size);
        BlockPos second = new BlockPos(position.getX() - size, position.getY() - size, position.getZ() - size);

        List<BlockPos> positions = new ArrayList<>();
        BlockPos.stream(second, first).forEachOrdered(blockPos -> {
            positions.add(blockPos.mutableCopy());
        });

        BlockPhysicsEntity.ofBlocks(context.getSource().getWorld(), positions);
        return 1;
    }
}
