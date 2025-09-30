package com.sp.destruction.client;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.client.custom.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientDestructionEvents {

    public static NukeDestructionClient NUKE_CLIENT = ClientDestructionEvent.register(new NukeDestructionClient());

    public static LaserDestructionClient CRACKS_CLIENT = ClientDestructionEvent.register(new LaserDestructionClient());

    public static PlanetDestructionClient PLANET_CLIENT = ClientDestructionEvent.register(new PlanetDestructionClient());

    public static SupernovaDestructionClient SUPERNOVA_CLIENT = ClientDestructionEvent.register(new SupernovaDestructionClient());

    public static BlackHoleDestructionClientPart2 BLACK_HOLE_CLIENT = ClientDestructionEvent.register(new BlackHoleDestructionClientPart2());

    public static void registerClientEvents() {
        DestroyingMinecraft.LOGGER.info("Registering client-side destruction events");
    }

}
