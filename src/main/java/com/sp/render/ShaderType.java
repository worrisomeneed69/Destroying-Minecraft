package com.sp.render;

import com.mojang.serialization.Codec;
import com.sp.DestroyingMinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SuppressWarnings("unused")
public enum ShaderType implements StringIdentifiable {
    NONE            ("none", false, false, false, false, null),
    NUKE            ("nuke", DestroyingMinecraftClient.nukePostShader.getPost()),
    CRACKS          ("orbital_laser", DestroyingMinecraftClient.cracksPostShader.getPost()),
    PLANET          ("planet", DestroyingMinecraftClient.planetPostShader.getPost()),
    SUPERNOVA       ("supernova", null),
    BLACK_HOLE      ("black_hole", true, false, true, true,  DestroyingMinecraftClient.blackHolePostShader.getPost());
//    MINI_BLACK_HOLE ("mini_black_hole", DestroyingMinecraft.idOf("mini_black_hole"))
//    INITIALIZE      ("init", DestroyingMinecraftClient.initializePostShader.getPost());

    public static final Codec<ShaderType> CODEC = StringIdentifiable.createCodec(ShaderType::values);
    final String id;
    final List<Identifier> enabledShaders;

    ShaderType(String id, @Nullable Identifier ... identifiers) {
        this(id, true, true, true, true, identifiers);
    }

    ShaderType(String id, boolean enableShadows, boolean enableSky, boolean enableBloom, boolean enablePost, @Nullable Identifier ... identifiers) {
        this.id = id;

        this.enabledShaders = new ArrayList<>();

        if(enableSky) this.enabledShaders.add(DestroyingMinecraftClient.supernovaPostShader.getPost());
        if(enableShadows) this.enabledShaders.add(DestroyingMinecraftClient.shadowPostShader.getPost());


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

    @Override
    public String asString() {
        return this.id;
    }

    public static ShaderType getFromString(String shader) {
        for (ShaderType type : ShaderType.values()) {
            if (shader.equals(type.id)) {
                return type;
            }
        }

        return ShaderType.NONE;
    }
}
