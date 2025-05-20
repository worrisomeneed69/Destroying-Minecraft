package com.sp.render;

import com.sp.DestroyingMinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SuppressWarnings("unused")
public enum ShaderType {
    NONE      (false, false, false, false, null),
    NUKE      (DestroyingMinecraftClient.nukePostShader.getPost()),
    CRACKS    (DestroyingMinecraftClient.cracksPostShader.getPost()),
    PLANET    (DestroyingMinecraftClient.planetPostShader.getPost()),
    SUPERNOVA (null),
    BLACK_HOLE(true, false, true, true,  DestroyingMinecraftClient.blackHolePostShader.getPost()),
    EARTH     (false, false, true, true, DestroyingMinecraftClient.earthPostShader.getPost());

    final List<Identifier> enabledShaders;

    ShaderType(@Nullable Identifier ... identifiers) {
        this(true, true, true, true, identifiers);
    }

    ShaderType(boolean enableShadows, boolean enableSky, boolean enableBloom, boolean enablePost, @Nullable Identifier ... identifiers) {
        this.enabledShaders = new ArrayList<>();

        if(enableShadows) this.enabledShaders.add(DestroyingMinecraftClient.shadowPostShader.getPost());
        if(enableSky) this.enabledShaders.add(DestroyingMinecraftClient.supernovaPostShader.getPost());

        //then add whatever shader after
        if(identifiers != null) {
            this.enabledShaders.addAll(Arrays.stream(identifiers).toList());
        }

        //finally add bloom and post to all of it if any shader is enabled
        if(enableBloom) this.enabledShaders.add(DestroyingMinecraftClient.bloomPostShader.getPost());
        if(enablePost) this.enabledShaders.add(DestroyingMinecraftClient.postProcessingPostShader.getPost());
    }

    public List<Identifier> getEnabledShaders() {
        return this.enabledShaders;
    }
}
