package com.sp;

import com.sp.render.PrevUniforms;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public class DestroyingMinecraftClient implements ClientModInitializer {
	private static final Identifier BLACK_HOLE_POST = Identifier.of(DestroyingMinecraft.MOD_ID, "black_hole");
	private static final Identifier BLACK_HOLE_SHADER = Identifier.of(DestroyingMinecraft.MOD_ID, "blackhole/black_hole");

	@Override
	public void onInitializeClient() {

		VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
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

			}

		});
	}
}