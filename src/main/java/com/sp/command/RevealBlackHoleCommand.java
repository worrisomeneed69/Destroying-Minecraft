package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

//TODO: Remove before gameplay
public class RevealBlackHoleCommand {
    private static final List<BlockPos> positions = new ArrayList<>();
    static {
        positions.add(new BlockPos(-32, 281, -60));
        positions.add(new BlockPos(-31, 281, -60));
        positions.add(new BlockPos(-30, 281, -60));
        positions.add(new BlockPos(-29, 281, -60));
        positions.add(new BlockPos(-28, 281, -60));
        positions.add(new BlockPos(-27, 281, -60));

        positions.add(new BlockPos(-31, 282, -60));
        positions.add(new BlockPos(-30, 282, -60));
        positions.add(new BlockPos(-29, 282, -60));
        positions.add(new BlockPos(-28, 282, -60));
        positions.add(new BlockPos(-27, 282, -60));

        positions.add(new BlockPos(-29, 283, -60));
        positions.add(new BlockPos(-28, 283, -60));
    }

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("revealblackhole")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .executes(RevealBlackHoleCommand::createPBE)
        );
    }

    private static int createPBE(CommandContext<ServerCommandSource> context) {
        BlockPhysicsEntity blockPhysicsEntity = BlockPhysicsEntity.ofBlocks(context.getSource().getWorld(), positions);
        blockPhysicsEntity.setVelocity(0, -0.5, 0);
        return 1;
    }
}
