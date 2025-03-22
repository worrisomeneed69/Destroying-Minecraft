package com.sp.mixin.sodiumcompat;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.DestroyingMinecraft;
import com.sp.render.ShadowMapRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.caffeinemc.mods.sodium.client.gl.shader.*;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.*;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = ShaderChunkRenderer.class, remap = false)
public abstract class SodiumShadowMapShaderMixin {
    @Unique GlProgram<ChunkShaderInterface> shadowProgram;

    @Shadow protected abstract GlProgram<ChunkShaderInterface> compileProgram(ChunkShaderOptions options);

    @Unique
    private final Map<ChunkShaderOptions, GlProgram<ChunkShaderInterface>> shadowPrograms = new Object2ObjectOpenHashMap<>();

    @Redirect(method = "begin", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/ShaderChunkRenderer;compileProgram(Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderOptions;)Lnet/caffeinemc/mods/sodium/client/gl/shader/GlProgram;"))
    private GlProgram<ChunkShaderInterface> redirectShadowShader(ShaderChunkRenderer instance, ChunkShaderOptions options, @Local(argsOnly = true) TerrainRenderPass pass){
        if(ShadowMapRenderer.isRenderingShadowMap()){

            GlProgram<ChunkShaderInterface> program = this.shadowPrograms.get(options);

            if (program == null) {
                this.shadowPrograms.put(options, program = this.createShadowShader("blocks/shadow_layer_opaque", options));
            }

            return program;

        }

        return this.compileProgram(options);
    }

    @Unique
    private GlProgram<ChunkShaderInterface> createShadowShader(String path, ChunkShaderOptions options){
        ShaderConstants constants = options.constants();

        GlShader vertShader = ShaderLoader.loadShader(ShaderType.VERTEX,
                DestroyingMinecraft.idOf(path + ".vsh"), constants);

        GlShader fragShader = ShaderLoader.loadShader(ShaderType.FRAGMENT,
                DestroyingMinecraft.idOf(path + ".fsh"), constants);

        try {
            return GlProgram.builder(DestroyingMinecraft.idOf("chunk_shader"))
                    .attachShader(vertShader)
                    .attachShader(fragShader)
                    .bindAttribute("a_Position", ChunkShaderBindingPoints.ATTRIBUTE_POSITION)
                    .bindAttribute("a_Color", ChunkShaderBindingPoints.ATTRIBUTE_COLOR)
                    .bindAttribute("a_TexCoord", ChunkShaderBindingPoints.ATTRIBUTE_TEXTURE)
                    .bindAttribute("a_LightAndData", ChunkShaderBindingPoints.ATTRIBUTE_LIGHT_MATERIAL_INDEX)
                    .bindFragmentData("fragColor", ChunkShaderBindingPoints.FRAG_COLOR)
                    .link((shader) -> new DefaultShaderInterface(shader, options));
        } finally {
            vertShader.delete();
            fragShader.delete();
        }
    }

}
