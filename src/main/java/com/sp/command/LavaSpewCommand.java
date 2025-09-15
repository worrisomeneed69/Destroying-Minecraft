package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.networking.ServerPacketManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LavaSpewCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("lavaspew")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(argument("position", Vec3ArgumentType.vec3())
                                    .executes(context ->
                                            createPBE(
                                                    context,
                                                    Vec3ArgumentType.getVec3(context, "position")
                                            )
                                    )
                        )
        );
    }

    private static int createPBE(CommandContext<ServerCommandSource> context, Vec3d position) {
        for (PlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPacketManager.sendLavaSpewPacket(player, position);
        }
        return 1;
    }
}
