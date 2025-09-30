package com.sp.render;

import com.mojang.serialization.Codec;
import com.sp.DestroyingMinecraftClient;
import com.sp.destruction.DestructionType;
import com.sp.destruction.client.ClientDestructionEvents;
import com.sp.render.postshaders.PostShader;
import com.sp.render.postshaders.PostShaders;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ShaderType implements StringIdentifiable {
    NONE            ("none", false, false, false, false, null),
    NUKE            ("nuke", ClientDestructionEvents.NUKE_CLIENT.getPostShader()),
    CRACKS          ("orbital_laser", ClientDestructionEvents.CRACKS_CLIENT.getPostShader()),
    PLANET          ("planet", ClientDestructionEvents.PLANET_CLIENT.getPostShader()),
    SUPERNOVA       ("supernova", null),
    BLACK_HOLE      ("black_hole", true, false, true, true,  ClientDestructionEvents.BLACK_HOLE_CLIENT.getPostShader());

    public static final Codec<ShaderType> CODEC = StringIdentifiable.createCodec(ShaderType::values);
    final String id;
    final List<PostShader> enabledShaders;

    ShaderType(String id, @Nullable PostShader ... postShaders) {
        this(id, true, true, true, true, postShaders);
    }

    ShaderType(String id, boolean enableShadows, boolean enableSky, boolean enableBloom, boolean enablePost, @Nullable PostShader ... postShaders) {
        this.id = id;

        this.enabledShaders = new ArrayList<>();

        if(enableSky) this.enabledShaders.add(ClientDestructionEvents.SUPERNOVA_CLIENT.getPostShader());
        if(enableShadows) this.enabledShaders.add(PostShaders.SHADOW);


        //then add whatever shader after
        if(postShaders != null) {
            this.enabledShaders.addAll(Arrays.stream(postShaders).toList());
        }

        //finally add bloom and post to all of it if any shader is enabled
        if(enableBloom) this.enabledShaders.add(PostShaders.BLOOM);
        if(enablePost) this.enabledShaders.add(PostShaders.POST);
    }

    public List<PostShader > getEnabledShaders() {
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

    public static ShaderType getFromDestructionType(DestructionType type) {
        return switch (type) {
            case NUKE -> ShaderType.NUKE;
            case ORBITAL_LASER -> ShaderType.CRACKS;
            case PLANET -> ShaderType.PLANET;
            case SUPERNOVA -> ShaderType.SUPERNOVA;
            case BLACK_HOLE -> ShaderType.BLACK_HOLE;
        };
    }
}
