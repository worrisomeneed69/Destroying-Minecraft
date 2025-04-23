package com.sp;

import com.sp.networking.InitializePackets;
import com.sp.render.CameraShake;
import com.sp.render.PrevUniforms;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.blackhole.BlockInstanceRenderer;
import com.sp.render.nuke.NukeRenderer;
import com.sp.render.supernova.SupernovaRenderer;
import com.sp.util.BetterUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class DestroyingMinecraftClient implements ClientModInitializer {
	public BlockInstanceRenderer blockInstanceRenderer;
	public static NukeRenderer nukeRenderer = new NukeRenderer(400);
	public static SupernovaRenderer supernovaRenderer = new SupernovaRenderer(100);

	private static final Identifier BLACK_HOLE_POST = DestroyingMinecraft.idOf("black_hole");
	private static final Identifier BLACK_HOLE_SHADER = DestroyingMinecraft.idOf("blackhole/black_hole");

	private static final Identifier SHADOWS_POST = DestroyingMinecraft.idOf("shadows");
	private static final Identifier SHADOWS_SHADER = DestroyingMinecraft.idOf("shadows/shadows");

	private static final Identifier SKY_POST = DestroyingMinecraft.idOf("sky");
	private static final Identifier SKY_SHADER = DestroyingMinecraft.idOf("sky/sky");

	@Override
	public void onInitializeClient() {
//		INSTANCE = this;
		InitializePackets.registerClientNetworking();

		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;

			if(clientWorld != null) {
				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
					if (camera != null) {
						ShadowMapRenderer.renderShadowMap(camera);
					}
				}

				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SKY){
					if(this.blockInstanceRenderer == null){
						this.blockInstanceRenderer = new BlockInstanceRenderer();
					}
//					blockInstanceRenderer.render();
				}
			}
		}));

		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;
			float tickDelta = client.getRenderTickCounter().getTickDelta(true);

			if(clientWorld != null) {
				if (BLACK_HOLE_POST.equals(name)) {
					ShaderProgram shaderProgram = context.getShader(BLACK_HOLE_SHADER);
					if (shaderProgram != null) {
						if (PrevUniforms.isInitialized()) {
							BetterUniforms.setMatrix(shaderProgram, "prevProjMat", PrevUniforms.getPrevProjMat());
							BetterUniforms.setMatrix(shaderProgram, "prevViewMat", PrevUniforms.getPrevModelViewMat());
							BetterUniforms.setVector(shaderProgram, "prevCameraPos", PrevUniforms.getPrevCameraPos());

						}

						PrevUniforms.update();
					}

				} else if (SHADOWS_POST.equals(name)) {
					ShaderProgram shaderProgram = context.getShader(SHADOWS_SHADER);
					if (shaderProgram != null) {
						ShadowMapRenderer.setShadowUniforms(shaderProgram, clientWorld);
						supernovaRenderer.setUniforms(shaderProgram, tickDelta);
						nukeRenderer.setUniforms(shaderProgram, tickDelta);
					}
				} else if (SKY_POST.equals(name)) {
					ShaderProgram shaderProgram = context.getShader(SKY_SHADER);
					if (shaderProgram != null) {

						Matrix4f matrix4f = new Matrix4f();
						//Supernova
//						matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
//						matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees((clientWorld.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F) - 90.0f));

						//Nuke
						matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees(-20.0f));
						matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(0.0f));
						BetterUniforms.setMatrix(shaderProgram, "sunMat", matrix4f);

						supernovaRenderer.setUniforms(shaderProgram, tickDelta);
					}
				} else if(nukeRenderer.POST.equals(name)){
					ShaderProgram shaderProgram = context.getShader(nukeRenderer.SHADER);
					if (shaderProgram != null) {
						nukeRenderer.setUniforms(shaderProgram, tickDelta);
					}
				}
			}

		});

		ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
			this.blockInstanceRenderer.free();
			this.blockInstanceRenderer = null;
		});

		//Update rendering
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
			for(CameraShake cameraShake : CameraShake.getAllInstances()){
				cameraShake.individualTick();
			}
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
			supernovaRenderer.updateTimer();
			nukeRenderer.updateTimer();
		});
	}
}