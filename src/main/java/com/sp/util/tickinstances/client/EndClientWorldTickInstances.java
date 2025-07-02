package com.sp.util.tickinstances.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

import java.util.Vector;

/**
 * Extensions of this class will pass through a tick method that will be called at the end of every client world tick
 */
@Environment(EnvType.CLIENT)
public abstract class EndClientWorldTickInstances {
    private static final Vector<EndClientWorldTickInstances> allInstances = new Vector<>();

    public EndClientWorldTickInstances() {
        allInstances.add(this);
    }


    public abstract void tickClientWorld(World world);

    public static Vector<EndClientWorldTickInstances> getInstances() {
        return allInstances;
    }

}
