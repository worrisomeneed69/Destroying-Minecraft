package com.sp.config;

import com.sp.DestroyingMinecraftClient;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class DestroyingMinecraftConfig extends MidnightConfig {
    public static final String SHADERS = "shaders";

    @Entry(category = SHADERS)
    public static ShaderType shaderType = ShaderType.NONE;



    public enum ShaderType {
        NONE(),
        NUKE(DestroyingMinecraftClient.shadowPostShader.getPost(), DestroyingMinecraftClient.nukePostShader.getPost(), DestroyingMinecraftClient.BLOOM_POST),
        PLANET(DestroyingMinecraftClient.planetPostShader.getPost()),
        SUPERNOVA(DestroyingMinecraftClient.shadowPostShader.getPost(), DestroyingMinecraftClient.supernovaPostShader.getPost(), DestroyingMinecraftClient.BLOOM_POST),
        BLACK_HOLE(DestroyingMinecraftClient.shadowPostShader.getPost(), DestroyingMinecraftClient.blackHolePostShader.getPost(), DestroyingMinecraftClient.BLOOM_POST);

        final List<Identifier> enabledShaders;

        ShaderType(Identifier ... identifiers){
            this.enabledShaders = Arrays.stream(identifiers).toList();
        }

        public List<Identifier> getEnabledShaders(){
            return this.enabledShaders;
        }
    }
}
