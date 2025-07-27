package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.render.gui.hud.DestructionTitleRenderCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.sp.command.DestructionCommand.*;

public class InvokeDestructionPacket {
    private static final Identifier nukeSmokeEmitterId = DestroyingMinecraft.idOf("nuke_smoke");

    public static void receive(DestructionPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()->{
            boolean on = payload.start();

            switch (payload.type()){
                case nukeType: {
//                    DestroyingMinecraftClient.nukePostShader.getRenderTimer().toggleExplosion(on);

//                    if(on) {
//                        try {
//                            ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
//                            ParticleEmitter emitter = manager.createEmitter(nukeSmokeEmitterId);
//                            emitter.setPosition(-1007, 70, 1056);
//                            manager.addParticleSystem(emitter);
//                        } catch (Exception ignored) {
//
//                        }
//                    }
                    break;
                }

                case orbitalLaserType: {
                    DestroyingMinecraftClient.cracksPostShader.getDestructionEvent().setActive(on);
                    if (on) DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.ORBITAL_LASER_ANIMATION);
                    break;
                }

                case planetType: {
                    DestroyingMinecraftClient.planetPostShader.getDestructionEvent().setActive(on);
                    if (on) DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.PLANET_ANIMATION);
                    break;
                }

                case supernovaJazz: {
                    DestroyingMinecraftClient.supernovaPostShader.supernovaJazz.setActive(on);
                    break;
                }

                case supernovaType: {
                    DestroyingMinecraftClient.supernovaPostShader.getDestructionEvent().setActive(on);
                    if (on) DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.SUPERNOVA_ANIMATION);
                    break;
                }

                case blackHolePart1Type: {
                    DestroyingMinecraftClient.blackHolePostShader.getDestructionEvent().setActive(on);
                    if (on) DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.BLACK_HOLE_ANIMATION);
                    break;
                }

                case blackHolePart2Type: {
                    DestroyingMinecraftClient.blackHolePostShader.destruction2.setActive(on);
                    break;
                }
            }

        });
    }


    public record DestructionPayload(boolean start, int type) implements CustomPayload {
        public static final CustomPayload.Id<DestructionPayload> ID = new CustomPayload.Id<>(DestroyingMinecraft.idOf("dest"));

        public static final PacketCodec<RegistryByteBuf, DestructionPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.BOOL, DestructionPayload::start,
                PacketCodecs.INTEGER, DestructionPayload::type,
                DestructionPayload::new);


        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
