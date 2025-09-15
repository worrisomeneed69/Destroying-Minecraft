package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.networking.CustomPayloads;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.CameraShakeInstance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.util.Easing;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class LavaSpewPacket {
    private static final Random random = Random.create();
    private static final Identifier LAVA_EMITTER = DestroyingMinecraft.idOf("lava");

    public static void receive(CustomPayloads.LavaSpewPacketPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            MinecraftClient client = context.client();
            if (client.world == null) return;

            int numOfParticles = random.nextBetween(100,150);
            Vector3f pos = payload.position();

            try {
                ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
                ParticleEmitter emitter = manager.createEmitter(LAVA_EMITTER);
                if (emitter != null) {
                    emitter.setPosition(pos.x, pos.y, pos.z);
                    manager.addParticleSystem(emitter);
                }

            } catch (Exception e) {

            }


            for (int i = 0; i < numOfParticles; i++) {
                float spreadX = random.nextFloat()*(2.0f - 1.0f) * 0.2f;
                float spreadZ = random.nextFloat()*(2.0f - 1.0f) * 0.2f;
                client.world.addParticle(ParticleTypes.POOF, pos.x, pos.y, pos.z, spreadX, random.nextFloat()*0.5, spreadZ);
            }
        });
    }

}
