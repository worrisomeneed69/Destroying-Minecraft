package com.sp.mixin;

import com.sp.DestroyingMinecraftClient;
import com.sp.mixininterfaces.CullingDataCache;
import com.sp.render.blackhole.BlockInstanceRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements CullingDataCache {
    @Shadow
    @Final
    @Mutable
    private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks;

    @Unique
    private ObjectArrayList<ChunkBuilder.BuiltChunk> savedRenderChunks = new ObjectArrayList<>(69696);


    @Shadow
    private double lastCameraPitch;

    @Shadow
    private double lastCameraYaw;

    @Unique
    private double savedLastCameraPitch;

    @Unique
    private double savedLastCameraYaw;

    @Override
    public void saveState() {
        swap();
    }

    @Override
    public void restoreState() {
        swap();
    }

    @Unique
    private void swap() {
        ObjectArrayList<ChunkBuilder.BuiltChunk> tmpList = builtChunks;
        builtChunks = savedRenderChunks;
        savedRenderChunks = tmpList;
        double tmp;

        tmp = lastCameraPitch;
        lastCameraPitch = savedLastCameraPitch;
        savedLastCameraPitch = tmp;

        tmp = lastCameraYaw;
        lastCameraYaw = savedLastCameraYaw;
        savedLastCameraYaw = tmp;
    }

//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", ordinal = 0, shift = At.Shift.AFTER))
//    private void renderBHTerrain(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci){
//        if(DestroyingMinecraftClient.INSTANCE.blockInstanceRenderer == null){
//            DestroyingMinecraftClient.INSTANCE.blockInstanceRenderer = new BlockInstanceRenderer();
//        }
//        DestroyingMinecraftClient.INSTANCE.blockInstanceRenderer.render();
//    }


}
