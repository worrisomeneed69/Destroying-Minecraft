package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.networking.InitializePackets;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class InvokeDestructionPacket {
    private static final Identifier nukeSmokeEmitterId = DestroyingMinecraft.idOf("nuke_smoke");

    public static void receive(InitializePackets.DestructionPayload payload, ClientPlayNetworking.Context context){
        context.client().execute(()->{
            boolean on = payload.start() == 1;

            switch (payload.type()){
                case 0: DestroyingMinecraftClient.supernovaPostShader.getRenderTimer().toggleExplosion(on); break;
                case 1: {
                    DestroyingMinecraftClient.nukePostShader.getRenderTimer().toggleExplosion(on);

                    if(on) {
                        try {
                            ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
                            ParticleEmitter emitter = manager.createEmitter(nukeSmokeEmitterId);
                            emitter.setPosition(-1007, 70, 1056);
                            manager.addParticleSystem(emitter);
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                }

                case 2: DestroyingMinecraftClient.planetPostShader.getRenderTimer().toggleExplosion(on); break;
            }

        });
    }

}
