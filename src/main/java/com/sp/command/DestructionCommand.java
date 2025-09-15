package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.networking.CustomPayloads;
import com.sp.networking.ServerPacketManager;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;

public class DestructionCommand {
    //Correlates to the switch statement in the InvokeDestructionPacket
    public final static int reset = 0;
    public final static int nukeType = 1;
    public final static int orbitalLaserType = 2;
    public final static int planetType = 3;
    public final static int supernovaJazz = 4;
    public final static int supernovaType = 5;
    public final static int blackHolePart1Type = 6;
    public final static int blackHolePart2Type = 7;
    public final static int initializeType = 8;

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("destruction")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("supernova")
                                .then(CommandManager.literal("jazz")
                                        .executes(commandContext -> execute(commandContext, supernovaJazz))
                                )
                                .executes(commandContext -> execute(commandContext, supernovaType))
                        )


                        .then(CommandManager.literal("nuke")
                                .executes(commandContext -> execute(commandContext, nukeType))
                        )


                        .then(CommandManager.literal("planet")
                                .executes(commandContext -> execute(commandContext, planetType))
                        )

                        .then(CommandManager.literal("black_hole")
                                .then(CommandManager.literal("select")
                                        .then(argument("position", BlockPosArgumentType.blockPos())
                                                .executes(commandContext -> blackHoleSelect(commandContext, BlockPosArgumentType.getBlockPos(commandContext, "position")))
                                        )
                                )
                                .then(CommandManager.literal("part1")
                                        .executes(commandContext -> blackHoleExecute(commandContext, blackHolePart1Type))
                                )
                                .then(CommandManager.literal("part2")
                                        .executes(commandContext -> blackHoleExecute(commandContext, blackHolePart2Type))
                                )
                        )

                        .then(CommandManager.literal("orbital_laser")
                                    .executes(commandContext -> execute(commandContext, orbitalLaserType))
                        )

                        .then(CommandManager.literal("explosion")
                                .then(CommandManager.literal("directional")
                                        .then(CommandManager.argument("length", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("width", IntegerArgumentType.integer(0))
                                                        .then(CommandManager.argument("angle", FloatArgumentType.floatArg())
                                                                .then(CommandManager.argument("density", FloatArgumentType.floatArg(0.0f))
                                                                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                                            .executes(commandContext -> directionalSpinningBlockExplosion(
                                                                                    commandContext.getSource().getWorld(),
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
                                                                        commandContext.getSource().getWorld(),
                                                                        IntegerArgumentType.getInteger(commandContext, "radius"),
                                                                        FloatArgumentType.getFloat(commandContext, "density"),
                                                                        Vec3ArgumentType.getVec3(commandContext, "location")
                                                                ))
                                                        )
                                                )
                                        )
                                )

                        )
                        .then(CommandManager.literal("initialize")
                                .executes(commandContext -> execute(commandContext, initializeType))
                        )
                        .then(CommandManager.literal("reset")
                                .executes(DestructionCommand::reset)
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, int type) {
        List<ServerPlayerEntity> playerList = context.getSource().getWorld().getPlayers();
        WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.getSource().getWorld());
        BlockPos spawnPointPos = BlockPos.ORIGIN;

        long startTime = System.currentTimeMillis();
        for(ServerPlayerEntity player : playerList) {
            ServerPlayNetworking.send(player, new CustomPayloads.DestructionPayload(type, startTime));

            switch (type) {
                case planetType -> {
                    spawnPointPos = new BlockPos(-1496, 66, 1205);
                }
                case supernovaType -> {
                    spawnPointPos = new BlockPos(-1048, 76, 1328);
                }
                case supernovaJazz -> {
                    PlayerComponent component = InitializeComponents.PLAYERS.get(player);
                    component.resetPlayer();
                    ServerPacketManager.sendWaitingRoomPacket(player, false);
                }
                case orbitalLaserType -> {
                    spawnPointPos = new BlockPos(-1705, 67, 1560);
                }
            }
            player.setSpawnPoint(context.getSource().getWorld().getRegistryKey(), spawnPointPos, 0, true, false);
        }

        switch (type) {
            case planetType -> {
                worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.planetServerDestruction, startTime);
            }
            case supernovaType -> {
                worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.supernovaServerDestruction, startTime);
            }
            case orbitalLaserType -> {
                worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.laserDestruction, startTime);
            }
            case initializeType -> {
                worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.initializeDestruction, startTime);
            }
        }
        return 1;
    }

    private static int blackHoleSelect(CommandContext<ServerCommandSource> context, BlockPos centerPos) {
        int i = BlackHoleDestruction.selectSurfaceBlocks(centerPos, context.getSource().getWorld());
        context.getSource().sendFeedback(() -> Text.literal("Successfully selected " + i + " blocks for destruction"), true);
        return 1;
    }

    private static int blackHoleExecute(CommandContext<ServerCommandSource> context, int part) {
        WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.getSource().getWorld());

        switch (part) {
            case blackHolePart1Type -> worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.blackHoleDestructionPart1, System.currentTimeMillis());
            case blackHolePart2Type -> worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraft.blackHoleDestructionPart2, System.currentTimeMillis());
        }

        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            player.setSpawnPoint(context.getSource().getWorld().getRegistryKey(), new BlockPos(-1150, 71, 363), 0, true, false);
            ServerPlayNetworking.send(player, new CustomPayloads.DestructionPayload(part, System.currentTimeMillis()));
        }

        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> context) {
        WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.getSource().getWorld());
        for (DestructionEvent event : ServerDestructionEvent.getAllServerInstances()) {
            event.setActive(false, -1);
            event.resetEvent();
        }

        BlackHoleDestruction.setStartDestruction(false);
        BlackHoleDestruction.reset();

        worldComponent.setGravityLerp(0.0);
        worldComponent.syncLight();

        for (ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new CustomPayloads.DestructionPayload(reset, -1));
        }

        return 1;
    }

    private static int directionalSpinningBlockExplosion(ServerWorld world, int length, int width, float angle, float density, Vec3d position) {
        DirectionalSBE explosion = new DirectionalSBE(length, width, angle, density, position);
        explosion.beginExplosion(world);

        return 1;
    }

    private static int pointSpinningBlockExplosion(ServerWorld world, int radius, float density, Vec3d position) {
        PointSBE explosion = new PointSBE(radius, density, position);
        explosion.beginExplosion(world);

        return 1;
    }
}
