package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.networking.S2C.InvokeDestructionPacket;
import com.sp.world.BlackHoleDestruction;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;

public class DestructionCommand {
    //Correlates to the switch statement in the InvokeDestructionPacket
    final static int nukeType = 0;
    final static int orbitalLaserType = 1;
    final static int supernovaType = 2;
    final static int planetType = 3;
    final static int blackHoleType = 4;

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

                        .then(CommandManager.literal("black_hole")
                                .then(CommandManager.literal("select")
                                        .then(argument("position", BlockPosArgumentType.blockPos())
                                                .executes(commandContext -> blackHoleSelect(commandContext, BlockPosArgumentType.getBlockPos(commandContext, "position")))
                                        )
                                )
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> blackHoleExecute(commandContext, true))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> blackHoleExecute(commandContext, false))
                                )
                        )

                        .then(CommandManager.literal("orbital_laser")
                                .then(CommandManager.literal("start")
                                        .executes(commandContext -> execute(commandContext, true, orbitalLaserType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> execute(commandContext, false, orbitalLaserType))
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

    private static int execute(CommandContext<ServerCommandSource> context, boolean start, int type) {
        List<ServerPlayerEntity> playerList = context.getSource().getWorld().getPlayers();

        for(ServerPlayerEntity player : playerList) {
            ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(start, type));
        }

        switch (type) {
            case supernovaType -> {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                executorService.schedule(() -> {
//                    for(ServerPlayerEntity player : playerList) {
//                        ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(start, type));
//                    }

                    DirectionalSBE explosion = new DirectionalSBE(50, 50, -90, 0.5f, new Vec3d(-1720, 74, 1595));
                    explosion.beginExplosion();
                    executorService.shutdown();
                }, 144000, TimeUnit.MILLISECONDS);
                break;
            }
        }
        return 1;
    }

    private static int blackHoleSelect(CommandContext<ServerCommandSource> context, BlockPos centerPos) {
        int i = BlackHoleDestruction.selectSurfaceBlocks(centerPos, context.getSource().getWorld());
        context.getSource().sendFeedback(() -> Text.literal("Successfully selected " + i + " blocks for destruction"), true);
        return 1;
    }

    private  static int blackHoleExecute(CommandContext<ServerCommandSource> context, boolean start) {
//        if (start) {
//            BlackHoleDestruction.setStartDestruction(true);
//        } else {
//            BlackHoleDestruction.setStartDestruction(false);
//            BlackHoleDestruction.reset(context.getSource().getWorld());
//        }

        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(start, blackHoleType));
        }

        return 1;
    }

    private static int directionalSpinningBlockExplosion(int length, int width, float angle, float density, Vec3d position) {
        DirectionalSBE explosion = new DirectionalSBE(length, width, angle, density, position);
        explosion.beginExplosion();

        return 1;
    }

    private static int pointSpinningBlockExplosion(int radius, float density, Vec3d position) {
        PointSBE explosion = new PointSBE(radius, density, position);
        explosion.beginExplosion();

        return 1;
    }
}
