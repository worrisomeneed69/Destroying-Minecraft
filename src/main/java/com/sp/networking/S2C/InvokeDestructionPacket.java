package com.sp.networking.S2C;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.DestructionType;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.networking.CustomPayloads;
import com.sp.render.ShaderType;
import com.sp.render.gui.hud.DestructionTitleRenderCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class InvokeDestructionPacket {

    public static void receive(CustomPayloads.DestructionPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            WorldDestructionEventsComponent worldComponent = InitializeComponents.EVENTS.get(context.client().world);

            if (payload.name().equals("reset")) {
                for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                    event.setActive(false, -1);
                    event.resetEvent();
                }
                return;
            }

            Optional<DestructionType> type = DestructionType.getFromName(payload.name());

            type.ifPresent(destructionType -> {
                for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                    if (event.getDestructionType().equals(destructionType)) {
                        worldComponent.setAndStartCurrentDestructionEvent(event, payload.startTime());
                        worldComponent.setDestructionEventPosition(new Vec3d(payload.position()));
                        ShaderType shaderType = ShaderType.getFromDestructionType(destructionType);
                        if (shaderType != DestroyingMinecraftConfig.shaderType) {
                            DestroyingMinecraftConfig.shaderType = shaderType;
                            DestroyingMinecraftConfig.write(DestroyingMinecraft.MOD_ID);
                        }
                        break;
                    }
                }
                DestructionTitleRenderCallback.setDestructionTitle(destructionType);
            });

        });
    }

}
