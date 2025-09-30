package com.sp.destruction.server;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.custom.BlackHoleDestructionServerPart2;
import com.sp.destruction.server.custom.LaserDestructionServer;
import com.sp.destruction.server.custom.PlanetDestructionServer;
import com.sp.destruction.server.custom.SupernovaDestructionServer;

public class ServerDestructionEvents {

    public static LaserDestructionServer CRACKS_SERVER = ServerDestructionEvent.register(new LaserDestructionServer());

    public static PlanetDestructionServer PLANET_SERVER = ServerDestructionEvent.register(new PlanetDestructionServer());

    public static SupernovaDestructionServer SUPERNOVA_SERVER = ServerDestructionEvent.register(new SupernovaDestructionServer());

    public static BlackHoleDestructionServerPart2 BLACK_HOLE_SERVER = ServerDestructionEvent.register(new BlackHoleDestructionServerPart2());

    public static void registerServerEvents() {
        DestroyingMinecraft.LOGGER.info("Registering server-side destruction events");
    }

}
