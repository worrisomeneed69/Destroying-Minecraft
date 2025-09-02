package com.sp.networking.S2C;

import com.sp.DestroyingMinecraftClient;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.networking.CustomPayloads;
import com.sp.render.gui.hud.DestructionTitleRenderCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import static com.sp.command.DestructionCommand.*;

public class InvokeDestructionPacket {

    public static void receive(CustomPayloads.DestructionPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.client().world);

            switch (payload.type()) {
                case reset: {
                    for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                        event.setActive(false, -1);
                        event.resetEvent();
                    }
                    break;
                }

                case nukeType: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.nukePostShader.getDestructionEvent(), payload.startTime());
                    break;
                }

                case orbitalLaserType: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.cracksPostShader.getDestructionEvent(), payload.startTime());
                    DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.ORBITAL_LASER_ANIMATION);
                    break;
                }

                case planetType: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.planetPostShader.getDestructionEvent(), payload.startTime());
                    DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.PLANET_ANIMATION);
                    break;
                }

                case supernovaJazz: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.supernovaPostShader.supernovaJazz, payload.startTime());
                    break;
                }

                case supernovaType: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.supernovaPostShader.getDestructionEvent(), payload.startTime());
                    DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.SUPERNOVA_ANIMATION);
                    break;
                }

                case blackHolePart1Type: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.blackHolePostShader.getDestructionEvent(), payload.startTime());
                    DestructionTitleRenderCallback.setDestructionTitle(DestructionTitleRenderCallback.BLACK_HOLE_ANIMATION);
                    break;
                }

                case blackHolePart2Type: {
                    worldComponent.setAndStartCurrentDestructionEvent(DestroyingMinecraftClient.blackHolePostShader.destruction2, payload.startTime());
                    break;
                }
            }

        });
    }

}
