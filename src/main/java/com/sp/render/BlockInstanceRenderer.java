package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.DestroyingMinecraft;
import com.sp.render.materialsampler.CustomRenderLayersAndVertexFormats;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;


import static foundry.veil.impl.client.render.dynamicbuffer.DynamicBufferManger.MAIN_WRAPPER;

public class BlockInstanceRenderer {
    VertexBuffer vertexBuffer;
    private static final Identifier shaderPath = DestroyingMinecraft.idOf("blackhole/blackholeterrain/blackholeterrain");
    private static final Identifier dirtTexture = Identifier.ofVanilla("textures/block/dirt.png");
    private static final Identifier stoneTexture = Identifier.ofVanilla("textures/block/stone.png");
    private static final Identifier gravelTexture = Identifier.ofVanilla("textures/block/gravel.png");
    private static final Identifier deepslateTexture = Identifier.ofVanilla("textures/block/deepslate.png");




    public BlockInstanceRenderer() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, CustomRenderLayersAndVertexFormats.POSITION_TEXTURE_NORMAL);

        this.createCube(bufferBuilder, 0, 0, 0, 1, 1, 1);

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }

    public void render() {
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(MAIN_WRAPPER);
        if(fbo == null) return;

        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if(shader == null) return;

        fbo.bind(false);

        Vector3f position = new Vector3f(0,70,0);

        RenderSystem.setShaderTexture(0, dirtTexture);
        RenderSystem.setShaderTexture(1, stoneTexture);
        RenderSystem.setShaderTexture(2, gravelTexture);
        RenderSystem.setShaderTexture(3, deepslateTexture);
        shader.setSampler("Sampler0", RenderSystem.getShaderTexture(0));
        shader.setSampler("Sampler1", RenderSystem.getShaderTexture(1));
        shader.setSampler("Sampler2", RenderSystem.getShaderTexture(2));
        shader.setSampler("Sampler3", RenderSystem.getShaderTexture(3));

        BetterUniforms.setVector(shader, "offset", position);

        shader.bindSamplers(0);
        shader.setDefaultUniforms(VertexFormat.DrawMode.QUADS);


        this.vertexBuffer.bind();
        shader.bind();

        VeilRenderSystem.drawInstanced(this.vertexBuffer, 1000);

        ShaderProgram.unbind();
        VertexBuffer.unbind();


        AdvancedFbo.unbind();
    }


    private void createCube(BufferBuilder bufferBuilder, float pMinX, float pMinY, float pMinZ, float pMaxX, float pMaxY, float pMaxZ) {

        //NORTH
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMinZ).mul(2)).texture(1.0f, 1.0f).normal(0,0,-1);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMinZ).mul(2)).texture(0.0f, 1.0f).normal(0,0,-1);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMinZ).mul(2)).texture(0.0f, 0.0f).normal(0,0,-1);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMinZ).mul(2)).texture(1.0f, 0.0f).normal(0,0,-1);

        //DOWN
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMinZ).mul(2)).texture(0.0f, 1.0f).normal(0,-1,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMaxZ).mul(2)).texture(0.0f, 0.0f).normal(0,-1,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMaxZ).mul(2)).texture(1.0f, 0.0f).normal(0,-1,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMinZ).mul(2)).texture(1.0f, 1.0f).normal(0,-1,0);

        //UP
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMaxZ).mul(2)).texture(0.0f, 1.0f).normal(0,1,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMaxZ).mul(2)).texture(0.0f, 0.0f).normal(0,1,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMinZ).mul(2)).texture(1.0f, 0.0f).normal(0,1,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMinZ).mul(2)).texture(1.0f, 1.0f).normal(0,1,0);

        //SOUTH
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMaxZ).mul(2)).texture(0.0f, 1.0f).normal(0,0,1);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMaxZ).mul(2)).texture(0.0f, 0.0f).normal(0,0,1);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMaxZ).mul(2)).texture(1.0f, 0.0f).normal(0,0,1);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMaxZ).mul(2)).texture(1.0f, 1.0f).normal(0,0,1);

        //EAST
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMaxZ).mul(2)).texture(0.0f, 1.0f).normal(1,0,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMaxZ).mul(2)).texture(0.0f, 0.0f).normal(1,0,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMinY, pMinZ).mul(2)).texture(1.0f, 0.0f).normal(1,0,0);
        bufferBuilder.vertex(new Vector3f(pMaxX, pMaxY, pMinZ).mul(2)).texture(1.0f, 1.0f).normal(1,0,0);

        //WEST
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMinZ).mul(2)).texture(0.0f, 1.0f).normal(-1,0,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMinZ).mul(2)).texture(0.0f, 0.0f).normal(-1,0,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMinY, pMaxZ).mul(2)).texture(1.0f, 0.0f).normal(-1,0,0);
        bufferBuilder.vertex(new Vector3f(pMinX, pMaxY, pMaxZ).mul(2)).texture(1.0f, 1.0f).normal(-1,0,0);
    }

    public void free(){
        this.vertexBuffer.close();
    }

}
