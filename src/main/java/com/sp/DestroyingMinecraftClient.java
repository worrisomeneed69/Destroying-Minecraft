package com.sp;

import com.sp.render.PrevUniforms;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DestroyingMinecraftClient implements ClientModInitializer {
	private static final Identifier BLACK_HOLE_POST = Identifier.of(DestroyingMinecraft.MOD_ID, "black_hole");
	private static final Identifier SHADOWS_POST = Identifier.of(DestroyingMinecraft.MOD_ID, "shadows");
	private static final Identifier SHADOWS_SHADER = Identifier.of(DestroyingMinecraft.MOD_ID, "shadows/shadows");
	private static final Identifier BLACK_HOLE_SHADER = Identifier.of(DestroyingMinecraft.MOD_ID, "blackhole/black_hole");

	@Override
	public void onInitializeClient() {

		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;

			if(clientWorld != null) {
				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
					if (camera != null) {
						ShadowMapRenderer.renderShadowMap(camera);
					}
				}
			}
		}));

		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			World clientWorld = client.world;

			if(BLACK_HOLE_POST.equals(name)){
				ShaderProgram shaderProgram = context.getShader(BLACK_HOLE_SHADER);
				if(shaderProgram != null){
					if(PrevUniforms.isInitialized()) {
						shaderProgram.setMatrix("prevProjMat", PrevUniforms.getPrevProjMat());
						shaderProgram.setMatrix("prevViewMat", PrevUniforms.getPrevModelViewMat());
						shaderProgram.setVector("prevCameraPos", PrevUniforms.getPrevCameraPos());
					}

					PrevUniforms.update();
				}

			} else if(SHADOWS_POST.equals(name)){
				ShaderProgram shaderProgram = context.getShader(SHADOWS_SHADER);
				if(shaderProgram != null && clientWorld != null){
					ShadowMapRenderer.setShadowUniforms(shaderProgram, clientWorld);
				}
			}

		});
	}
}