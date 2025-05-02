package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.networking.InitializePackets;
import com.sp.world.spinningblockexplosion.DirectionalSpinningBlockExplosion;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class DestructionCommand {
    //Correlates to the switch statement in the InvokeDestructionPacket
    final static int supernovaType = 0;
    final static int nukeType = 1;
    final static int planetType = 2;

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("destruction")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("supernova")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, 1, supernovaType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, 0, supernovaType))
                                )
                        )


                        .then(CommandManager.literal("nuke")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, 1, nukeType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, 0, nukeType))
                                )
                        )


                        .then(CommandManager.literal("planet")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, 1, planetType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, 0, planetType))
                                )
                        )

                        .then(CommandManager.literal("explosion")
                                .then(CommandManager.argument("length", IntegerArgumentType.integer(0))
                                        .then(CommandManager.argument("width", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("angle", FloatArgumentType.floatArg())
                                                        .then(CommandManager.argument("density", FloatArgumentType.floatArg(0.0f))
                                                                .executes(commandContext -> spinningBlockExplosion(
                                                                        commandContext,
                                                                        IntegerArgumentType.getInteger(commandContext, "length"),
                                                                        IntegerArgumentType.getInteger(commandContext, "width"),
                                                                        FloatArgumentType.getFloat(commandContext, "angle"),
                                                                        FloatArgumentType.getFloat(commandContext, "density")

                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, int start, int type){
        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new InitializePackets.DestructionPayload(start, type));
        }
        return 1;
    }

    private static int spinningBlockExplosion(CommandContext<ServerCommandSource> context, int length, int width, float angle, float density){
        DirectionalSpinningBlockExplosion explosion = new DirectionalSpinningBlockExplosion(length, width, angle, density, context.getSource().getPlayer().getPos());
        explosion.beginExplosion();

        return 1;
    }
}
