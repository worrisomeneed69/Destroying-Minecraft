package com.sp.render;

import com.sp.DestroyingMinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ShaderType {
    NONE      (false, null),
    NUKE      (true,  DestroyingMinecraftClient.nukePostShader.getPost()),
    CRACKS    (true,  DestroyingMinecraftClient.cracksPostShader.getPost()),
    PLANET    (true,  DestroyingMinecraftClient.planetPostShader.getPost()),
    SUPERNOVA (true,  null),
    BLACK_HOLE(true,  DestroyingMinecraftClient.blackHolePostShader.getPost()),
    EARTH     (false, DestroyingMinecraftClient.earthPostShader.getPost());

    final List<Identifier> enabledShaders;

    ShaderType(boolean useUniversal, @Nullable Identifier ... identifiers) {
        this.enabledShaders = new ArrayList<>();

        //Shadows and sky are universal
        if(useUniversal) {
            this.enabledShaders.add(DestroyingMinecraftClient.shadowPostShader.getPost());
            this.enabledShaders.add(DestroyingMinecraftClient.supernovaPostShader.getPost());
        }

        //then add whatever shader after
        if(identifiers != null) {
            this.enabledShaders.addAll(Arrays.stream(identifiers).toList());
        }

        //finally add bloom and post to all of it
        this.enabledShaders.add(DestroyingMinecraftClient.bloomPostShader.getPost());
        this.enabledShaders.add(DestroyingMinecraftClient.postProcessingPostShader.getPost());
    }

    public List<Identifier> getEnabledShaders() {
        return this.enabledShaders;
    }
}
