package com.sp;

import com.sp.config.DestroyingMinecraftConfig;
import com.sp.entity.ModEntities;
import com.sp.entity.client.renderer.BlockPhysicsEntityRenderer;
import com.sp.entity.client.renderer.SpinningBlockEntityRenderer;
import com.sp.mixin.PostProcessingManagerAccessor;
import com.sp.networking.InitializePackets;
import com.sp.render.CameraShake;
import com.sp.render.CustomDynamicBuffers;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.postshaders.PostShader;
import com.sp.render.postshaders.custom.*;
import com.sp.render.rendertimers.blackhole.BlockInstanceRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class DestroyingMinecraftClient implements ClientModInitializer {
	public BlockInstanceRenderer blockInstanceRenderer;

	public static NukePostShader nukePostShader = new NukePostShader();
	public static CracksPostShader cracksPostShader = new CracksPostShader();
	public static PlanetPostShader planetPostShader = new PlanetPostShader();
	public static SupernovaPostShader supernovaPostShader = new SupernovaPostShader();
	public static BlackHolePostShader blackHolePostShader = new BlackHolePostShader();
	public static ShadowPostShader shadowPostShader = new ShadowPostShader();

	public static final Identifier BLOOM_POST = DestroyingMinecraft.idOf("bloom");

	private static DestroyingMinecraftConfig.ShaderType prevShaderType;
	private static final Set<Identifier> removedPipelines = new HashSet<>(1);

	@Override
	public void onInitializeClient() {
		InitializePackets.registerClientNetworking();

		EntityRendererRegistry.register(ModEntities.SPINNING_BLOCK, SpinningBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLOCK_PHYSICS_ENTITY, BlockPhysicsEntityRenderer::new);

		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;

			if(clientWorld != null) {
				//Only render the shadow map with shaders that need it
				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
					if (camera != null) {
						ShadowMapRenderer.renderShadowMap(camera);
					}
				}

				//Only render the black hole terrain when rendering the black hole
				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SKY){
					if(this.blockInstanceRenderer == null){
						this.blockInstanceRenderer = new BlockInstanceRenderer();
					}

					if(DestroyingMinecraftConfig.shaderType == DestroyingMinecraftConfig.ShaderType.BLACK_HOLE) {
						blockInstanceRenderer.render();
					}
				}
			}


			//Remove all the shaders currently in the pipeline then add back the ones we need in their specific order
			if(stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
				//Only update when the shaderType changes
				if(prevShaderType != DestroyingMinecraftConfig.shaderType) {
					this.updatePostShader();
					prevShaderType = DestroyingMinecraftConfig.shaderType;
				}
			}

		}));

		//Set the uniforms for all the post shaders
		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			float tickDelta = client.getRenderTickCounter().getTickDelta(true);

			if(clientWorld != null) {
				for(PostShader postShader : PostShader.getAllInstances()) {

					if(postShader.getPost().equals(name)) {
						ShaderProgram shaderProgram = context.getShader(postShader.getShader());
						if (shaderProgram != null) {
							postShader.setUniforms(shaderProgram, tickDelta, client, clientWorld);
						}
					}

				}
			}

		});

		ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
			this.blockInstanceRenderer.free();
			this.blockInstanceRenderer = null;
		});

		//Update camera shakes
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
			for(CameraShake cameraShake : CameraShake.getAllInstances()){
				cameraShake.individualTick();
			}
		});

		//Update every render timer
		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
			for(PostShader postShader : PostShader.getAllInstances()) {
				if(postShader.getRenderTimer() != null){
					postShader.getRenderTimer().updateTimer(clientWorld);
				}
			}
		});
	}

	private void updatePostShader(){
		PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
		removedPipelines.clear();

		//Remove all shaders
		((PostProcessingManagerAccessor)postProcessingManager).getActuallyActivePipelines().forEach(profileEntry -> {
			removedPipelines.add(profileEntry.getPipeline());
		});

		for (Identifier id : removedPipelines){
			postProcessingManager.remove(id);
		}



		//Enable all shaders in their specific order
		for (Identifier enabledPosts : DestroyingMinecraftConfig.shaderType.getEnabledShaders()) {
			if (!postProcessingManager.isActive(enabledPosts)) {
				postProcessingManager.add(enabledPosts);
			}
		}
	}

	private boolean needsShadowMap() {
		DestroyingMinecraftConfig.ShaderType type = DestroyingMinecraftConfig.shaderType;
		return type == DestroyingMinecraftConfig.ShaderType.BLACK_HOLE || type == DestroyingMinecraftConfig.ShaderType.SUPERNOVA || type == DestroyingMinecraftConfig.ShaderType.NUKE;
	}
}