package com.sp;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.block.entity.ModBlockEntities;
import com.sp.block.entity.client.PhysicsDoorBlockRenderer;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.destruction.client.ClientDestructionEvents;
import com.sp.destruction.client.custom.NukeDestructionClient;
import com.sp.entity.ModEntities;
import com.sp.entity.client.model.StarPiercerModel;
import com.sp.entity.client.renderer.BlockPhysicsEntityRenderer;
import com.sp.entity.client.renderer.MeteorEntityRenderer;
import com.sp.entity.client.renderer.SpinningBlockEntityRenderer;
import com.sp.entity.client.renderer.StarPiercerEntityRenderer;
import com.sp.item.ModModelPredicates;
import com.sp.mixin.PostProcessingManagerAccessor;
import com.sp.networking.InitializePackets;
import com.sp.networking.callbacks.ClientConnectionEvents;
import com.sp.render.*;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.gui.HSVColorTextureManager;
import com.sp.render.gui.hud.DestructionTitleRenderCallback;
import com.sp.render.gui.hud.PlayZoneWarningRenderCallback;
import com.sp.render.postshaders.PostShader;
import com.sp.render.postshaders.PostShaders;
import com.sp.render.postshaders.custom.*;
import com.sp.util.RenderUtil;
import com.sp.util.tickinstances.client.ClientTickInstances;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import foundry.veil.Veil;
import foundry.veil.api.client.registry.RenderTypeShardRegistry;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.impl.client.render.dynamicbuffer.DynamicBufferShard;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DestroyingMinecraftClient implements ClientModInitializer {
    private static final Identifier BLOOM_FRAMEBUFFER = DestroyingMinecraft.idOf("bloom/bloom_start");
    private static final Identifier TRANSLUCENT_FRAMEBUFFER = DestroyingMinecraft.idOf("translucent");
	public static boolean shouldRenderDebug = false;
	public BlockInstanceRenderer blockInstanceRenderer;

	private static ShaderType prevShaderType;
	private static final Set<Identifier> removedPipelines = new HashSet<>(1);
	private static boolean enabledDynamicBuffers = false;

    public static int destructionDistance = Integer.MAX_VALUE;


	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new DestructionTitleRenderCallback());
		HudRenderCallback.EVENT.register(new PlayZoneWarningRenderCallback());

		InitializePackets.registerClientNetworking();
		ClientTickInstances.registerAllClientTickInstances();
		ModModelPredicates.registerModelPredicates();
        ClientDestructionEvents.registerClientEvents();
        PostShaders.registerPostShaders();

        EntityRendererRegistry.register(ModEntities.SPINNING_BLOCK, SpinningBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLOCK_PHYSICS_ENTITY, BlockPhysicsEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.METEOR_ENTITY, MeteorEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(StarPiercerModel.STAR_PIERCER_MODEL_LAYER, StarPiercerModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.STAR_PIERCER_ENTITY, StarPiercerEntityRenderer::new);

		BlockEntityRendererFactories.register(ModBlockEntities.PHYSICS_DOOR_BE, PhysicsDoorBlockRenderer::new);

        RenderTypeShardRegistry.addGenericShard(renderType -> "translucent_target".equals(getOutputName(renderType)), new DynamicBufferShard("translucent", DestroyingMinecraftClient::getTranslucentBuffer));

        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
            if(!enabledDynamicBuffers){
                this.enableDynamicBuffers();
                enabledDynamicBuffers = true;
            }

            MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			RenderSystem.disableDepthTest();

            if (client.player != null) {
                PlayerComponent component = InitializeComponents.PLAYERS.get(client.player);

                if (BlackScreenManager.isBlackScreen() || component.isInWaitingRoom()) {
                    client.options.hudHidden = true;
                }
            }


			if(clientWorld == null || VeilLevelPerspectiveRenderer.isRenderingPerspective()) return;

			switch (stage) {
				case AFTER_LEVEL -> {
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

                case AFTER_BLOCK_ENTITIES -> {
                    if (this.blockInstanceRenderer == null) {
                        this.blockInstanceRenderer = new BlockInstanceRenderer();
                    }

                    //Only render the black hole terrain when rendering the black hole
                    if (DestroyingMinecraftConfig.shaderType == ShaderType.BLACK_HOLE) {
                        blockInstanceRenderer.render();
                    }

                    TranslucentRenderer.bind(GlConst.GL_DEPTH_BUFFER_BIT);

                    if (client.player == null) return;

                    PlayerComponent component = InitializeComponents.PLAYERS.get(client.player);
                    if (shouldRenderDebug) {
                        BlackHoleDestruction.renderSelectionDebug(matrixStack.toPoseStack(), bufferSource, camera, frustum);
                    }

                    if(shouldRenderDebug || !component.isInsideAPlayZone()) {


                        for (PlayZone playZone : PlayZoneManager.getActivePlayZones()) {
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

                    SelectionHandler.renderSelection(matrixStack.toPoseStack(), bufferSource, deltaTracker, camera);
                }

				case AFTER_WEATHER -> {
                    TranslucentRenderer.unbind();
				}
			}

		});

		//Set the uniforms for all the post shaders
		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			float tickDelta = client.getRenderTickCounter().getTickDelta(true);

			if(clientWorld != null) {
                ShaderType type = DestroyingMinecraftConfig.shaderType;

				for(PostShader postShader : enabledShadersFor(type)) {
					if(postShader.getPost().equals(name)) {
						postShader.setUniforms(context, tickDelta, client, clientWorld);
					}
				}
			}
		});

        ClientConnectionEvents.DISCONNECT.register(client -> {

			this.blockInstanceRenderer.free();
			this.blockInstanceRenderer = null;

            BlackHoleDestruction.clear();
            if (!client.isIntegratedServerRunning()) {
                PlayZoneManager.clearAllPlayZones();
            }

            for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                event.resetAnimations();
            }

            if (client.world != null) {
                WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(client.world);
                if (component.getCurrentDestructionEvent() != null) {
                    component.getCurrentDestructionEvent().resetEvent();
                }
                component.setAndStartCurrentDestructionEvent(null, -1);
            }
		});

		//Update camera shakes
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
			CameraShakeManager.instancesTicks();
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
			SelectionHandler.tickClientWorld(clientWorld);
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
		for (PostShader enabledPosts : enabledShadersFor(DestroyingMinecraftConfig.shaderType)) {
			if (!postProcessingManager.isActive(enabledPosts.getPost())) {
				postProcessingManager.add(enabledPosts.getPost());
			}
		}
	}

    public static Framebuffer getTranslucentBuffer() {
        AdvancedFbo translucentBuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(TRANSLUCENT_FRAMEBUFFER);
        if (translucentBuffer != null && !VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            return translucentBuffer.toRenderTarget();
        }

        return MinecraftClient.getInstance().getFramebuffer();
    }

    private static String getOutputName(RenderLayer.MultiPhase renderType) {
        return VeilRenderType.getName(VeilRenderType.getShards(renderType).outputState());
    }

	private void enableDynamicBuffers() {
		Identifier bufferId = Veil.veilPath("forced");
		VeilRenderSystem.renderer().enableBuffers(bufferId, DynamicBufferType.values());
	}

    private static List<PostShader> enabledShadersFor(ShaderType type) {
        List<PostShader> list = new ArrayList<>();
        if (type.enableSky) list.add(ClientDestructionEvents.SUPERNOVA_CLIENT.getPostShader());
        if (type.enableShadows) list.add(PostShaders.SHADOW);
        switch (type) {
            case NUKE -> list.add(ClientDestructionEvents.NUKE_CLIENT.getPostShader());
            case CRACKS -> list.add(ClientDestructionEvents.CRACKS_CLIENT.getPostShader());
            case BLACK_HOLE -> list.add(ClientDestructionEvents.BLACK_HOLE_CLIENT.getPostShader());
            default -> {}
        }
        if (type.enableBloom) list.add(PostShaders.BLOOM);
        if (type.enablePost) list.add(PostShaders.POST);
        return list;
    }
}
