package com.sp.destruction.server;

import com.sp.destruction.DestructionEvent;
import com.sp.destruction.DestructionType;
import com.sp.destruction.client.ClientDestructionEvent;
import net.minecraft.world.World;

import java.util.Vector;

/**
 * Handles all the destruction events server side, including spawning entities and destroying blocks
 */
public abstract class ServerDestructionEvent extends DestructionEvent {
    private static final Vector<DestructionEvent> serverInstances = new Vector<>();

    public ServerDestructionEvent(DestructionType destructionType, int duration) {
        super(destructionType, duration,false);
        serverInstances.add(this);
    }

    public static <T extends ServerDestructionEvent> T register(T event) {
        serverInstances.add(event);
        return event;
    }

    public static synchronized Vector<DestructionEvent> getAllServerInstances() {
        return (Vector<DestructionEvent>) serverInstances.clone();
    }
}
