package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AddPlayZoneCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("playzone")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(CommandManager.literal("add")
                                .then(argument("position1", BlockPosArgumentType.blockPos())
                                        .then(argument("position2", BlockPosArgumentType.blockPos())
                                                .executes(context ->
                                                        createPlayZone(
                                                                context,
                                                                BlockPosArgumentType.getBlockPos(context, "position1"),
                                                                BlockPosArgumentType.getBlockPos(context, "position2")
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(argument("position", Vec3ArgumentType.vec3())
                                        .executes(context ->
                                                removePlayZone(
                                                        context,
                                                        Vec3ArgumentType.getVec3(context, "position")
                                                )
                                        )
                                )
                        )
        );
    }



    private static int createPlayZone(CommandContext<ServerCommandSource> context, BlockPos position1, BlockPos position2) {
        PlayZone playZone = new PlayZone(position1, position2);
        PlayZoneManager.addPlayZone(context.getSource().getWorld(), playZone);
//        context.getSource().sendFeedback(() -> Text.literal("Created a new play zone"), true);

        return 1;
    }

    private static int removePlayZone(CommandContext<ServerCommandSource> context, Vec3d position) {
        int numOfPlayZones = PlayZoneManager.removeAllPlayZonesAtPos(position, context.getSource().getWorld());
        context.getSource().sendFeedback(() -> Text.literal("Removed " + numOfPlayZones + " play zone" + (numOfPlayZones > 1 ? "s" : "")), true);


        return numOfPlayZones;
    }

}
