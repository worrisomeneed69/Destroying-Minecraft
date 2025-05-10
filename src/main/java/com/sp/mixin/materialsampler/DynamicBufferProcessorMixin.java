package com.sp.mixin.materialsampler;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.CustomDynamicBuffers;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import foundry.veil.impl.client.render.dynamicbuffer.DynamicBufferProcessor;
import io.github.ocelot.glslprocessor.api.GlslInjectionPoint;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(DynamicBufferProcessor.class)
public class DynamicBufferProcessorMixin {

    //@Inject(method = "modify", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/dynamicbuffer/DynamicBufferType;getName()Ljava/lang/String;", ordinal = 4))
    private void setMaterialInShader(ShaderPreProcessor.Context ctx, GlslTree tree, CallbackInfo ci,
                                     @Local DynamicBufferType type,
                                     @Local(ordinal = 0) Map<String, GlslNode> markers,
                                     @Local(ordinal = 2) String shaderName,
                                     @Local(ordinal = 1) GlslNodeList treeBody,
                                     @Local(ordinal = 0) GlslNodeList mainFunctionBody,
                                     @Local(ordinal = 1) String output,
                                     @Local(ordinal = 2) boolean modified,
                                     @Local VertexFormat vertexFormat,
                                     @Local(ordinal = 1) Map<String, Object> data,
                                     @Local(ordinal = 3) boolean inVertex) throws GlslSyntaxException {

        if (type == CustomDynamicBuffers.MATERIAL_BUFFER && !markers.containsKey("veil:" + CustomDynamicBuffers.MATERIAL_BUFFER.getName())) {

            if (vertexFormat.has(VertexFormatElement.NORMAL)) {
                if (ctx.isVertex()) {
                    Optional<GlslNewFieldNode> fieldOptional = tree.field(vertexFormat.getName(VertexFormatElement.NORMAL));
                    if (fieldOptional.isPresent()) {
                        treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("uniform mat3 NormalMat"));
                        treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("out vec3 Pass" + type.getSourceName()));
                        mainFunctionBody.add(new GlslAssignmentNode(new GlslVariableNode("Pass" + type.getSourceName()), GlslParser.parseExpression("NormalMat * " + fieldOptional.get().getName()), GlslAssignmentNode.Operand.EQUAL));
                        modified = true;
                        data.compute("mask", (s, o) -> (o instanceof Integer val ? val : 0) | type.getMask());
                    }
                } else if (ctx.isFragment()) {
                    if (inVertex) {
                        treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression("in vec3 Pass" + type.getSourceName()));
                        treeBody.add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression(output));
                        mainFunctionBody.add(new GlslAssignmentNode(new GlslVariableNode(type.getSourceName()), GlslParser.parseExpression("vec4(Pass" + type.getSourceName() + ", 1.0)"), GlslAssignmentNode.Operand.EQUAL));
                        modified = true;
                    }
                }
            }

        }
    }

}
