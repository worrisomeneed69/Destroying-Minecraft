package com.sp;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.block.entity.ModBlockEntities;
import com.sp.block.entity.client.PhysicsDoorBlockRenderer;
import com.sp.block.entity.client.voidblock.GlitchedVoidBlockEntityRenderer;
import com.sp.block.entity.client.voidblock.VoidBlockEntityRenderer;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.entity.ModEntities;
import com.sp.entity.client.model.StarPiercerModel;
import com.sp.entity.client.renderer.BlockPhysicsEntityRenderer;
import com.sp.entity.client.renderer.MeteorEntityRenderer;
import com.sp.entity.client.renderer.SpinningBlockEntityRenderer;
import com.sp.entity.client.renderer.StarPiercerEntityRenderer;
import com.sp.item.ModModelPredicates;
import com.sp.mixin.PostProcessingManagerAccessor;
import com.sp.networking.InitializePackets;
import com.sp.render.*;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.gui.HSVColorTextureManager;
import com.sp.render.gui.hud.DestructionTitleRenderCallback;
import com.sp.render.gui.hud.PlayZoneWarningRenderCallback;
import com.sp.render.gui.hud.WaitingRoomRenderCallback;
import com.sp.render.postshaders.PostShader;
import com.sp.render.postshaders.custom.*;
import com.sp.util.RenderUtil;
import com.sp.util.tickinstances.client.ClientTickInstances;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class DestroyingMinecraftClient implements ClientModInitializer {
	public static boolean shouldRenderDebug = false;
	public BlockInstanceRenderer blockInstanceRenderer;

	public static NukePostShader nukePostShader = new NukePostShader();
	public static CracksPostShader cracksPostShader = new CracksPostShader();
	public static PlanetPostShader planetPostShader = new PlanetPostShader();
	public static SupernovaPostShader supernovaPostShader = new SupernovaPostShader();
	public static BlackHolePostShader blackHolePostShader = new BlackHolePostShader();
	public static ShadowPostShader shadowPostShader = new ShadowPostShader();
	public static EarthPostShader earthPostShader = new EarthPostShader();
	public static BloomPostShader bloomPostShader = new BloomPostShader();
	public static PostProcessingPostShader postProcessingPostShader = new PostProcessingPostShader();

	private static ShaderType prevShaderType;
	private static final Set<Identifier> removedPipelines = new HashSet<>(1);

	private static boolean enabledDynamicBuffers = false;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new DestructionTitleRenderCallback());
		HudRenderCallback.EVENT.register(new PlayZoneWarningRenderCallback());
		HudRenderCallback.EVENT.register(new WaitingRoomRenderCallback());

		InitializePackets.registerClientNetworking();
		ClientTickInstances.registerAllClientTickInstances();
		ModModelPredicates.registerModelPredicates();


		EntityRendererRegistry.register(ModEntities.SPINNING_BLOCK, SpinningBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLOCK_PHYSICS_ENTITY, BlockPhysicsEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.METEOR_ENTITY, MeteorEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(StarPiercerModel.STAR_PIERCER_MODEL_LAYER, StarPiercerModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.STAR_PIERCER_ENTITY, StarPiercerEntityRenderer::new);

		BlockEntityRendererFactories.register(ModBlockEntities.VOID_BE, VoidBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.GLITCHED_VOID_BE, GlitchedVoidBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.PHYSICS_DOOR_BE, PhysicsDoorBlockRenderer::new);
//		BlockEntityRendererFactories.register(ModBlockEntities.LIMBO_SQUARE_BE, LimboSquareBlockEntityRenderer::new);

		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			RenderSystem.disableDepthTest();

            if (client.player != null) {
                PlayerComponent component = InitializeComponents.PLAYERS.get(client.player);

                if (BlackScreenManager.isBlackScreen() || component.isInWaitingRoom()) {
                    client.options.hudHidden = true;
                }
            }


			if(clientWorld == null || PerspectiveRenderer.isRenderingPerspective()) return;

			switch (stage) {
				case AFTER_LEVEL -> {
//					System.out.println(VeilRenderSystem.renderer().getCameraMatrices().getNearPlane());

					//Only render the shadow map with shaders that need it
					if (camera != null) {
						ShadowMapRenderer.renderShadowMap(camera);
					}

					//Remove all the shaders currently in the pipeline then add back the ones we need in their specific order
					//Only update when the shaderType changes
					if(prevShaderType != DestroyingMinecraftConfig.shaderType) {
						this.updatePostShader();
						prevShaderType = DestroyingMinecraftConfig.shaderType;
					}
				}
				case AFTER_SKY -> {
					if (this.blockInstanceRenderer == null) {
						this.blockInstanceRenderer = new BlockInstanceRenderer();
					}

					//Only render the black hole terrain when rendering the black hole
					if (DestroyingMinecraftConfig.shaderType == ShaderType.BLACK_HOLE) {
						blockInstanceRenderer.render();
					}
				}

				case AFTER_WEATHER -> {
					if(shouldRenderDebug) {
						BlackHoleDestruction.renderSelectionDebug(matrixStack.toPoseStack(), bufferSource, camera, frustum);

						Vector<PlayZone> activePlayZones = PlayZoneManager.getActivePlayZones();
						if (!activePlayZones.isEmpty() && client.player != null) {
							for (PlayZone playZone : activePlayZones) {
								boolean inside = playZone.isPositionInsideZone(client.player.pos);
								int[] colors = new int[4];
								colors[0] = inside ? 50 : 255;  //RED
								colors[1] = inside ? 255 : 50;  //GREEN
								colors[2] = 50;                 //BLUE
								colors[3] = 100;                //ALPHA

								RenderUtil.drawBox(
										matrixStack.toPoseStack(),
										bufferSource,
										playZone.getBoundingBox().offset(camera.getPos().negate()),
										colors[0],
										colors[1],
										colors[2],
										colors[3],
										true,
										playZone.isPositionInsideZone(camera.getPos())
								);

							}
						}
					}

					SelectionHandler.renderSelection(matrixStack.toPoseStack(), bufferSource, deltaTracker, camera);
				}
			}

		});

		//Set the uniforms for all the post shaders
		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			float tickDelta = client.getRenderTickCounter().getTickDelta(true);

			if(clientWorld != null) {
				for(PostShader postShader : PostShader.getAllInstances()) {

					if(postShader.getPost().equals(name)) {
						postShader.setUniforms(context, tickDelta, client, clientWorld);
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
			if(!enabledDynamicBuffers){
				this.enableDynamicBuffers();
				enabledDynamicBuffers = true;
			}

			CameraShakeManager.instancesTicks();
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
			SelectionHandler.tickClientWorld(clientWorld);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
			BlackHoleDestruction.clear();
		});


		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return DestroyingMinecraft.idOf("after_resources");
			}

			@Override
			public void reload(ResourceManager manager) {
				HSVColorTextureManager.init();
			}
		});
	}

	private void updatePostShader() {
		PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
		removedPipelines.clear();

		//Remove all shaders
		((PostProcessingManagerAccessor)postProcessingManager).getActuallyActivePipelines().forEach(profileEntry ->
				removedPipelines.add(profileEntry.getPipeline())
		);

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

	private void enableDynamicBuffers() {
		Identifier bufferId = Veil.veilPath("forced");
		VeilRenderSystem.renderer().enableBuffers(bufferId, DynamicBufferType.values());
	}
}