package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.networking.InitializePackets;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;


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
                                        .executes(commandContext -> execute(commandContext, true, supernovaType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, false, supernovaType))
                                )
                        )


                        .then(CommandManager.literal("nuke")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, true, nukeType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, false, nukeType))
                                )
                        )


                        .then(CommandManager.literal("planet")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, true, planetType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, false, planetType))
                                )
                        )

                        .then(CommandManager.literal("explosion")
                                .then(CommandManager.literal("directional")
                                        .then(CommandManager.argument("length", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("width", IntegerArgumentType.integer(0))
                                                        .then(CommandManager.argument("angle", FloatArgumentType.floatArg())
                                                                .then(CommandManager.argument("density", FloatArgumentType.floatArg(0.0f))
                                                                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                                            .executes(commandContext -> directionalSpinningBlockExplosion(
                                                                                    IntegerArgumentType.getInteger(commandContext, "length"),
                                                                                    IntegerArgumentType.getInteger(commandContext, "width"),
                                                                                    FloatArgumentType.getFloat(commandContext, "angle"),
                                                                                    FloatArgumentType.getFloat(commandContext, "density"),
                                                                                    Vec3ArgumentType.getVec3(commandContext, "location")
                                                                            ))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(CommandManager.literal("point")
                                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("density", FloatArgumentType.floatArg(0.0f))
                                                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                                .executes(commandContext -> pointSpinningBlockExplosion(
                                                                        IntegerArgumentType.getInteger(commandContext, "radius"),
                                                                        FloatArgumentType.getFloat(commandContext, "density"),
                                                                        Vec3ArgumentType.getVec3(commandContext, "location")
                                                                ))
                                                        )
                                                )
                                        )
                                )

                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, boolean start, int type){
        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new InitializePackets.DestructionPayload(start, type));
        }
        return 1;
    }

    private static int directionalSpinningBlockExplosion(int length, int width, float angle, float density, Vec3d position){
        DirectionalSBE explosion = new DirectionalSBE(length, width, angle, density, position);
        explosion.beginExplosion();

        return 1;
    }

    private static int pointSpinningBlockExplosion(int radius, float density, Vec3d position){
        PointSBE explosion = new PointSBE(radius, density, position);
        explosion.beginExplosion();

        return 1;
    }
}
