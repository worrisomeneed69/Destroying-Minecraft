package com.sp.destruction.server.custom;

import com.sp.destruction.DestructionEvent;
import com.sp.destruction.server.ServerDestructionEvent;
import net.minecraft.world.World;

public class PlanetDestructionServer extends ServerDestructionEvent {

    public PlanetDestructionServer() {
        super(1800);
    }
}
