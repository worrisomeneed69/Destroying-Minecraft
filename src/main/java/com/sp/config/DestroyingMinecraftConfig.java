package com.sp.config;

import com.sp.DestroyingMinecraftClient;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DestroyingMinecraftConfig extends MidnightConfig {
    public static final String SHADERS = "shaders";

    @Entry(category = SHADERS)
    public static ShaderType shaderType = ShaderType.NONE;



    public enum ShaderType {
        NONE(null),
        NUKE(DestroyingMinecraftClient.nukePostShader.getPost()),
        CRACKS(DestroyingMinecraftClient.cracksPostShader.getPost()),
        PLANET(DestroyingMinecraftClient.planetPostShader.getPost()),
        SUPERNOVA(),
        BLACK_HOLE(DestroyingMinecraftClient.blackHolePostShader.getPost());

        final List<Identifier> enabledShaders;

        ShaderType(@Nullable Identifier ... identifiers) {
            this.enabledShaders = new ArrayList<>();

            //Shadows and sky are universal, then add whatever shader after, finally add bloom to all of it
            if(identifiers != null) {
                this.enabledShaders.add(DestroyingMinecraftClient.shadowPostShader.getPost());
                this.enabledShaders.add(DestroyingMinecraftClient.supernovaPostShader.getPost());
                this.enabledShaders.addAll(Arrays.stream(identifiers).toList());
                this.enabledShaders.add(DestroyingMinecraftClient.BLOOM_POST);
            }
        }

        public List<Identifier> getEnabledShaders() {
            return this.enabledShaders;
        }
    }
}
