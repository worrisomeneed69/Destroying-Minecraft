package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.DestructionType;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.entity.custom.StarPiercerEntity;
import com.sp.networking.CustomPayloads;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import com.sp.world.spinningblockexplosion.custom.PointSBE;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;

public class DestructionCommand {
    private static final SimpleCommandExceptionType NO_STAR_PIERCERS_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("No Star Piercers exist nearby"));

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("destruction")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("type", DestructionArgumentTypes.destructionType())
                                .then(argument("position", Vec3ArgumentType.vec3())
                                        .executes(context ->
                                                execute(
                                                        context,
                                                        DestructionArgumentTypes.getDestructionType(context, "type"),
                                                        Vec3ArgumentType.getVec3(context, "position")
                                                )
                                        )
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
                        .then(CommandManager.literal("reset")
                                .executes(DestructionCommand::reset)
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, DestructionType type, Vec3d position) throws CommandSyntaxException {
        List<ServerPlayerEntity> playerList = context.getSource().getWorld().getPlayers();
        WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.getSource().getWorld());

        if (type == DestructionType.SUPERNOVA) {
            ServerWorld world = context.getSource().getWorld();
            Box box = Box.of(context.getSource().getPosition(), 500, 300, 500);
            List<StarPiercerEntity> starPiercerEntities = world.getEntitiesByClass(StarPiercerEntity.class, box, starPiercerEntity -> true);

            if (starPiercerEntities.isEmpty()) {
                throw NO_STAR_PIERCERS_EXCEPTION.create();
            }
        }

        long startTime = context.getSource().getWorld().getTime();
        for(ServerPlayerEntity player : playerList) {
            ServerPlayNetworking.send(player, new CustomPayloads.DestructionPayload(type.getName(), position.toVector3f(), startTime));
            player.setSpawnPoint(context.getSource().getWorld().getRegistryKey(), BlockPos.ofFloored(position), 0, true, false);
        }

        //Select Blocks for Black Hole Destruction
        if (type.equals(DestructionType.BLACK_HOLE)) {
            int i = BlackHoleDestruction.selectSurfaceBlocks(BlockPos.ofFloored(position), context.getSource().getWorld());
            context.getSource().sendFeedback(() -> Text.literal("Successfully selected " + i + " blocks for destruction"), true);
        }

        for (DestructionEvent event : ServerDestructionEvent.getAllServerInstances()) {
            if (event.getDestructionType().equals(type)) {
                worldComponent.setAndStartCurrentDestructionEvent(event, startTime);
                worldComponent.setDestructionEventPosition(position);
                break;
            }
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
        context.getSource().getWorld().getEntitiesByClass(
                BlockPhysicsEntity.class,
                Box.of(worldComponent.getDestructionEventPosition(), 1000, 1000, 1000),
                blockPhysicsEntity -> true).forEach(Entity::discard);

        worldComponent.setGravityLerp(0.0);
        worldComponent.syncLight();

        for (ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
            ServerPlayNetworking.send(player, new CustomPayloads.DestructionPayload("reset", new Vector3f(), -1));
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

    public static class DestructionArgumentTypes extends EnumArgumentType<DestructionType> {
        protected DestructionArgumentTypes() {
            super(DestructionType.CODEC, DestructionType::values);
        }

        public static EnumArgumentType<DestructionType> destructionType() {
            return new DestructionCommand.DestructionArgumentTypes();
        }

        public static DestructionType getDestructionType(CommandContext<ServerCommandSource> context, String id) {
            return context.getArgument(id, DestructionType.class);
        }
    }
}
