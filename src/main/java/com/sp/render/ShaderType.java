package com.sp.render;

import com.mojang.serialization.Codec;
import com.sp.destruction.DestructionType;
import net.minecraft.util.StringIdentifiable;

public enum ShaderType implements StringIdentifiable {
    NONE            ("none", false, false, false, false),
    NUKE            ("nuke", true, true, true, true),
    CRACKS          ("orbital_laser", true, true, true, true),
    PLANET          ("planet", true, true, true, true),
    SUPERNOVA       ("supernova", true, true, true, true),
    BLACK_HOLE      ("black_hole", true, false, true, true);

    public static final Codec<ShaderType> CODEC = StringIdentifiable.createCodec(ShaderType::values);
    final String id;
    public final boolean enableShadows;
    public final boolean enableSky;
    public final boolean enableBloom;
    public final boolean enablePost;

    ShaderType(String id, boolean enableShadows, boolean enableSky, boolean enableBloom, boolean enablePost) {
        this.id = id;
        this.enableShadows = enableShadows;
        this.enableSky = enableSky;
        this.enableBloom = enableBloom;
        this.enablePost = enablePost;
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

    public static ShaderType getFromDestructionType(DestructionType type) {
        return switch (type) {
            case NUKE -> ShaderType.NUKE;
            case ORBITAL_LASER -> ShaderType.CRACKS;
            case PLANET -> ShaderType.PLANET;
            case SUPERNOVA -> ShaderType.SUPERNOVA;
            case BLACK_HOLE -> ShaderType.BLACK_HOLE;
            default -> ShaderType.NONE;
        };
    }
}
