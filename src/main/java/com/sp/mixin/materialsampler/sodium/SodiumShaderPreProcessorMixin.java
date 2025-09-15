package com.sp.mixin.materialsampler.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.materialsampler.CustomDynamicBuffers;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import foundry.veil.impl.compat.sodium.SodiumShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslInjectionPoint;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeStringWriter;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SodiumShaderPreProcessor.class)
public class SodiumShaderPreProcessorMixin {

    @Inject(method = "modify", at = @At(value = "INVOKE", target = "Lio/github/ocelot/glslprocessor/api/visitor/GlslNodeStringWriter;visitTypeSpecifier(Lio/github/ocelot/glslprocessor/api/grammar/GlslTypeSpecifier;)V", shift = At.Shift.BY, by = 2), remap = false)
    private void setMaterialSampler(ShaderPreProcessor.Context ctx, GlslTree tree, CallbackInfo ci,
                                    @Local DynamicBufferType type,
                                    @Local List<GlslNode> mainBody,
                                    @Local GlslNodeList treeBody,
                                    @Local boolean modified,
                                    @Local String sourceName,
                                    @Local GlslNodeStringWriter writer,
                                    @Local(ordinal = 1) int i) throws GlslSyntaxException {
        if(type == CustomDynamicBuffers.MATERIAL_BUFFER) {
            String tempOutput = "layout(location = " + (1 + i) + ") out " + writer + " " + sourceName;
            if (ctx.isVertex()) {
//                treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("flat in int Material"));
                treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("flat out int material"));
                mainBody.add(GlslParser.parseExpression("material = 2"));
                modified = true;
                MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(modified); //Use the modified variable so it doesn't get thrown out
            }

            if (ctx.isFragment()) {
                treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("flat in int material"));
                treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression(tempOutput));
                mainBody.add(1, GlslParser.parseExpression(sourceName + " = ivec4(material, 0, 0, 1)"));
                modified = true;
                MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(modified); //Use the modified variable so it doesn't get thrown out
            }
        }
    }

}
