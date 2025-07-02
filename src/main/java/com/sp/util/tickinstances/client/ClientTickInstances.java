package com.sp.util.tickinstances.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public abstract class ClientTickInstances {

    public static void registerAllClientTickInstances() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (EndClientTickInstances instances : EndClientTickInstances.getInstances()) {
                instances.tickClient(client);
            }
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            for (EndClientWorldTickInstances instances : EndClientWorldTickInstances.getInstances()) {
                instances.tickClientWorld(world);
            }
        });
    }

}
