package com.sp.util.tickinstances.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.Vector;

/**
 * Extensions of this class will pass through a tick method that will be called at the end of every client tick
 */
@Environment(EnvType.CLIENT)
public abstract class EndClientTickInstances {
    private static final Vector<EndClientTickInstances> allInstances = new Vector<>();

    public EndClientTickInstances() {
        allInstances.add(this);
    }


    public abstract void tickClient(MinecraftClient client);

    public static Vector<EndClientTickInstances> getInstances() {
        return allInstances;
    }
}
