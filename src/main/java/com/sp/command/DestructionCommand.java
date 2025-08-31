package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.networking.S2C.InvokeDestructionPacket;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;

public class DestructionCommand {
    //Correlates to the switch statement in the InvokeDestructionPacket
    public final static int nukeType = 0;
    public final static int orbitalLaserType = 1;
    public final static int planetType = 2;
    public final static int supernovaJazz = 3;
    public final static int supernovaType = 4;
    public final static int blackHolePart1Type = 5;
    public final static int blackHolePart2Type = 6;

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("destruction")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("supernova")
                                .then(CommandManager.literal("start")
                                        .then(CommandManager.literal("jazz")
                                                .executes(commandContext -> execute(commandContext, true, supernovaJazz))
                                        )
                                        .executes(commandContext -> execute(commandContext, true, supernovaType))
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext ->
                                                execute(commandContext, false, supernovaType) +
                                                execute(commandContext, false, supernovaJazz)
                                        )
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
                                        .then(CommandManager.literal("part1")
                                                .executes(commandContext -> blackHoleExecute(commandContext, true, blackHolePart1Type))
                                        )
                                        .then(CommandManager.literal("part2")
                                                .executes(commandContext -> blackHoleExecute(commandContext, true, blackHolePart2Type))
                                        )
                                )
                                .then(CommandManager.literal("reset")
                                        .executes(commandContext -> blackHoleExecute(commandContext, false, 0))
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
            case planetType -> DestroyingMinecraft.planetServerDestruction.setActive(start);
            case supernovaType -> DestroyingMinecraft.supernovaServerDestruction.setActive(start);
            case orbitalLaserType -> DestroyingMinecraft.laserDestruction.setActive(start);
        }
        return 1;
    }

    private static int blackHoleSelect(CommandContext<ServerCommandSource> context, BlockPos centerPos) {
        int i = BlackHoleDestruction.selectSurfaceBlocks(centerPos, context.getSource().getWorld());
        context.getSource().sendFeedback(() -> Text.literal("Successfully selected " + i + " blocks for destruction"), true);
        return 1;
    }

    private  static int blackHoleExecute(CommandContext<ServerCommandSource> context, boolean start, int part) {
        if (start) {
            switch (part) {
                case blackHolePart1Type -> DestroyingMinecraft.blackHoleDestructionPart1.setActive(true);
                case blackHolePart2Type -> DestroyingMinecraft.blackHoleDestructionPart2.setActive(true);
            }
        } else {
            DestroyingMinecraft.blackHoleDestructionPart1.setActive(false);
            DestroyingMinecraft.blackHoleDestructionPart2.setActive(false);

            BlackHoleDestruction.setStartDestruction(false);
            BlackHoleDestruction.reset();

            WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(context.getSource().getWorld());
            component.setGravityLerp(0.0);
            component.sync();
        }

        for(ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            if (start) {
                ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(true, part));
            } else {
                ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(false, blackHolePart1Type));
                ServerPlayNetworking.send(player, new InvokeDestructionPacket.DestructionPayload(false, blackHolePart2Type));
            }

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
