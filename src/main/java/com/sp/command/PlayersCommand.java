package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.networking.ServerPacketManager;
import com.sp.render.ShaderType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayersCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("players")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(CommandManager.literal("changeshaders")
                                .then(argument("shader", ShaderTypeArgumentType.shaderType())
                                        .executes(context ->
                                                executeChangeShaders(
                                                        context,
                                                        ShaderTypeArgumentType.getShaderType(context, "shader")
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("setinwaitingroom")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("setinwaitingroom", BoolArgumentType.bool())
                                                .executes(context -> executeSetInWaitingRoom(
                                                                EntityArgumentType.getPlayers(context, "targets"),
                                                                BoolArgumentType.getBool(context, "setinwaitingroom")
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("reset")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(context -> executeReset(
                                                        context,
                                                        EntityArgumentType.getPlayers(context, "targets")
                                                )
                                        )
                                )
                        )
        );
    }



    private static int executeChangeShaders(CommandContext<ServerCommandSource> context, ShaderType shader) {
        for (ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPacketManager.sendShaderChangePacket(player, shader);
        }

        return 1;
    }

    private static int executeSetInWaitingRoom(Collection<ServerPlayerEntity> targets, boolean setInWaitingRoom) {
        if (targets.isEmpty()) {
            return -1;
        }

        for (ServerPlayerEntity player : targets) {
            PlayerComponent component = InitializeComponents.PLAYERS.get(player);
            component.setInWaitingRoom(setInWaitingRoom);
            ServerPacketManager.sendWaitingRoomPacket(player, setInWaitingRoom);
        }

        return targets.size();
    }

    private static int executeReset(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets) {
        for (ServerPlayerEntity player : targets) {
            PlayerComponent component = InitializeComponents.PLAYERS.get(player);
            component.resetPlayer();
            ServerPacketManager.sendWaitingRoomPacket(player, false);
        }

        return targets.size();
    }

    public static class ShaderTypeArgumentType extends EnumArgumentType<ShaderType> {
        protected ShaderTypeArgumentType() {
            super(ShaderType.CODEC, ShaderType::values);
        }

        public static EnumArgumentType<ShaderType> shaderType() {
            return new ShaderTypeArgumentType();
        }

        public static ShaderType getShaderType(CommandContext<ServerCommandSource> context, String id) {
            return context.getArgument(id, ShaderType.class);
        }
    }
}
