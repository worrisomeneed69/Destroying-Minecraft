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
import io.github.ocelot.glslprocessor.api.node.branch.GlslIfNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeStringWriter;
import io.github.ocelot.glslprocessor.impl.GlslParserImpl;
import io.github.ocelot.glslprocessor.impl.GlslTokenReader;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(SodiumShaderPreProcessor.class)
public class SodiumShaderPreProcessorMixin {
    @Unique private boolean once;

    //@Inject(method = "modify", at = @At("HEAD"), remap = false)
    private void setOnce(ShaderPreProcessor.Context ctx, GlslTree tree, CallbackInfo ci) {
        this.once = false;
    }

    //This is the most annoying method to mixin into :(
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

        if (ctx.isVertex() && !once) {
            treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("uniform int renderingShadow "));
            GlslTokenReader reader = new GlslTokenReader("vec3 distort(in vec3 shadowPosition) {\n" +
                    "    const float bias0 = 0.95;\n" +
                    "    const float bias1 = 1.0 - bias0;\n" +
                    "\n" +
                    "    float factorDistance = length(shadowPosition.xy);\n" +
                    "\n" +
                    "    float distortFactor = factorDistance * bias0 + bias1;\n" +
                    "\n" +
                    "    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);\n" +
                    "}");

            treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParserImpl.parseFunctionDefinition(reader));

            GlslTokenReader reader2 = new GlslTokenReader("(renderingShadow == 1)");
            List<GlslNode> nodeList = new ArrayList<>();
            nodeList.add(new GlslAssignmentNode(new GlslVariableNode("gl_Position.xyz"), new GlslVariableNode("distort(gl_Position.xyz)"), GlslAssignmentNode.Operand.EQUAL));
            mainBody.add(mainBody.size(), new GlslIfNode(GlslParserImpl.parseCondition(reader2), nodeList, Collections.emptyList()));
//            mainBody.add(mainBody.size() - 1, new GlslAssignmentNode(new GlslVariableNode("gl_Position.xyz"), new GlslVariableNode("distort(gl_Position.xyz)"), GlslAssignmentNode.Operand.EQUAL));
            modified = true;
            MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(modified); //Use the modified variable so it doesn't get thrown out
            this.once = true;
        }


    }

}
