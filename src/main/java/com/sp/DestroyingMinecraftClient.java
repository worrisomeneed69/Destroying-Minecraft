package com.sp;

import com.sp.mixininterfaces.SunMatrix;
import com.sp.render.CameraShake;
import com.sp.render.PrevUniforms;
import com.sp.render.ShadowMapRenderer;
import com.sp.render.blackhole.BlockInstanceRenderer;
import com.sp.render.supernova.SupernovaRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class DestroyingMinecraftClient implements ClientModInitializer {
	public static DestroyingMinecraftClient INSTANCE;
	public BlockInstanceRenderer blockInstanceRenderer;
	private static final Identifier BLACK_HOLE_POST = DestroyingMinecraft.idOf("black_hole");
	private static final Identifier BLACK_HOLE_SHADER = DestroyingMinecraft.idOf("blackhole/black_hole");

	private static final Identifier SHADOWS_POST = DestroyingMinecraft.idOf("shadows");
	private static final Identifier SHADOWS_SHADER = DestroyingMinecraft.idOf("shadows/shadows");

	private static final Identifier SKY_POST = DestroyingMinecraft.idOf("sky");
	private static final Identifier SKY_SHADER = DestroyingMinecraft.idOf("sky/sky");

	private static final Identifier SUPERNOVA_POST = DestroyingMinecraft.idOf("supernova");
	private static final Identifier SUPERNOVA_SHADER = DestroyingMinecraft.idOf("supernova/supernova");

	@Override
	public void onInitializeClient() {
		INSTANCE = this;

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
							shaderProgram.setMatrix("prevProjMat", PrevUniforms.getPrevProjMat());
							shaderProgram.setMatrix("prevViewMat", PrevUniforms.getPrevModelViewMat());
							shaderProgram.setVector("prevCameraPos", PrevUniforms.getPrevCameraPos());

						}

						PrevUniforms.update();
					}

				} else if (SHADOWS_POST.equals(name)) {
					ShaderProgram shaderProgram = context.getShader(SHADOWS_SHADER);
					if (shaderProgram != null) {
						ShadowMapRenderer.setShadowUniforms(shaderProgram, clientWorld);
						SupernovaRenderer.setSupernovaUniforms(shaderProgram, tickDelta);
					}
				} else if (SKY_POST.equals(name)) {
					ShaderProgram shaderProgram = context.getShader(SKY_SHADER);
					if (shaderProgram != null) {

						Matrix4f matrix4f = new Matrix4f();
						matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
						matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees((clientWorld.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F) - 90.0f));
						shaderProgram.setMatrix("sunMat", matrix4f);

						SupernovaRenderer.setSupernovaUniforms(shaderProgram, tickDelta);
					}
				} else if(SUPERNOVA_POST.equals(name)){
					ShaderProgram shaderProgram = context.getShader(SUPERNOVA_SHADER);
					if (shaderProgram != null) {
						Matrix4f matrix4f = new Matrix4f();
						matrix4f.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
						matrix4f.rotate(RotationAxis.POSITIVE_X.rotationDegrees((clientWorld.getSkyAngle(client.getRenderTickCounter().getTickDelta(true)) * 360.0F) - 90.0f));

						shaderProgram.setMatrix("sunMat", matrix4f);
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

			SupernovaRenderer.updateSupernovaTimer();
		});
	}
}