package com.sp.destruction.server;

import com.sp.destruction.DestructionEvent;
import net.minecraft.world.World;

import java.util.Vector;

/**
 * Handles all the destruction events server side, including spawning entities and destroying blocks
 */
public abstract class ServerDestructionEvent extends DestructionEvent {
    private static final Vector<DestructionEvent> serverInstances = new Vector<>();

    public ServerDestructionEvent(int duration) {
        super(duration,false);
        serverInstances.add(this);
    }

    public static synchronized Vector<DestructionEvent> getAllServerInstances() {
        return (Vector<DestructionEvent>) serverInstances.clone();
    }
}
