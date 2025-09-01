package com.sp.render;

import com.mojang.serialization.Codec;
import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import net.minecraft.util.BlockMirror;
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
    BLACK_HOLE      ("black_hole", true, false, true, true,  DestroyingMinecraftClient.blackHolePostShader.getPost()),
    MINI_BLACK_HOLE ("mini_black_hole", DestroyingMinecraft.idOf("mini_black_hole"));
//    EARTH           ("earth", false, false, true, true, DestroyingMinecraftClient.earthPostShader.getPost());

    public static final Codec<ShaderType> CODEC = StringIdentifiable.createCodec(ShaderType::values);
    final String id;
    final List<Identifier> enabledShaders;

    ShaderType(String id, @Nullable Identifier ... identifiers) {
        this(id, true, true, true, true, identifiers);
    }

    ShaderType(String id, boolean enableShadows, boolean enableSky, boolean enableBloom, boolean enablePost, @Nullable Identifier ... identifiers) {
        this.id = id;

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

    @Override
    public String asString() {
        return this.id;
    }

    public static ShaderType getFromString(String shader) {
        return switch (shader) {
            case "nuke" -> ShaderType.NUKE;
            case "orbital_laser" -> ShaderType.CRACKS;
            case "planet" -> ShaderType.PLANET;
            case "supernova" -> ShaderType.SUPERNOVA;
            case "black_hole" -> ShaderType.BLACK_HOLE;
            case "mini_black_hole" -> ShaderType.MINI_BLACK_HOLE;
            default -> ShaderType.NONE;
        };
    }
}
