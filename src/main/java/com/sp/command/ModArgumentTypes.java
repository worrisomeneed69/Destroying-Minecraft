package com.sp.command;

import com.sp.DestroyingMinecraft;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ModArgumentTypes {
    static {
        ArgumentTypeRegistry.registerArgumentType(
                DestroyingMinecraft.idOf("shader_type"),
                PlayersCommand.ShaderTypeArgumentType.class,
                ConstantArgumentSerializer.of(PlayersCommand.ShaderTypeArgumentType::shaderType)
        );
    }

    public static void registerModArgumentTypes() {
        DestroyingMinecraft.LOGGER.info("Registering command argument types for " + DestroyingMinecraft.MOD_ID);
    }
}
