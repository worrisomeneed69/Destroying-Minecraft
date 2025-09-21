package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.networking.CustomPayloads;
import com.sp.render.camerashake.CameraShakeManager;
import com.sp.render.camerashake.custom.PointCameraShake;
import com.sp.util.MathUtil;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PointSBEPacket {
    private static final Identifier SMOKE = DestroyingMinecraft.idOf("smoke");


    public static void receive(CustomPayloads.SBEPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            Vec3d pos = MathUtil.toVec3d(payload.position());
            PointCameraShake cameraShake = new PointCameraShake(pos, (float) payload.radius(), 40, Easing.LINEAR);
            CameraShakeManager.addCameraShake(cameraShake);

//            try {
//                ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
//                ParticleEmitter emitter = manager.createEmitter(SMOKE);
//                if (emitter != null) {
//                    emitter.setPosition(pos.x, pos.y + 1, pos.z);
//                    manager.addParticleSystem(emitter);
//                }
//            } catch (Exception e) {
//
//            }
        });
    }

}
