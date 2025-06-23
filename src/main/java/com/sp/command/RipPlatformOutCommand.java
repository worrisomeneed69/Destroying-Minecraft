package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.PhysicsBlockComponent;
import com.sp.entity.ModEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RipPlatformOutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("makeblockphysics")
                        .requires(source -> source.hasPermissionLevel(2)) // Permission level 2 (op)
                        .then(argument("position", BlockPosArgumentType.blockPos())
                                .then(argument("size", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            BlockPos position = BlockPosArgumentType.getBlockPos(context, "position");
                                            int size = IntegerArgumentType.getInteger(context, "size");

                                            BlockPhysicsEntity entity = new BlockPhysicsEntity(ModEntities.BLOCK_PHYSICS_ENTITY, source.getWorld());
                                            entity.setPosition(position.getX() + .5, position.getY() + .5, position.getZ() + .5);

                                            BlockPos first = new BlockPos(position.getX() + size, position.getY() + size, position.getZ() + size);
                                            BlockPos second = new BlockPos(position.getX() - size, position.getY() - size, position.getZ() - size);
                                            PhysicsBlockComponent component = InitializeComponents.PHYSICS_BLOCK.get(entity);

                                            BlockPos.iterate(second, first).forEach(blockPos -> {
                                                BlockState state = source.getWorld().getBlockState(blockPos);
                                                BlockPos relativePos = blockPos.subtract(position);
                                                System.out.println("WORKING 1");
                                                if (!state.isAir()) {
                                                    System.out.println("WORKING 1.5");
                                                    component.addBlock(new BlockPhysicsEntity.BlockData(state, relativePos));
                                                    System.out.println("WORKING 2");
                                                }
                                                System.out.println("WORKING 3");
                                                source.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
                                            });

                                            source.getWorld().spawnEntity(entity);
                                            source.sendFeedback(() -> Text.of("Spawned block physics entity at " + position.toShortString() + " with size " + size), true);
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
