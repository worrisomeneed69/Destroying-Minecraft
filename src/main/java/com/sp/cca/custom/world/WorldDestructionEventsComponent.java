package com.sp.cca.custom.world;

import com.sp.cca.InitializeComponents;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.destruction.server.ServerDestructionEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class WorldDestructionEventsComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    private final World world;
    private double gravityLerp;

    public WorldDestructionEventsComponent(World world) {
        this.world = world;
        this.gravityLerp = 0.0;
    }

    public double getGravityLerp() {
        return this.gravityLerp;
    }
    public void setGravityLerp(double gravityLerp) {
        this.gravityLerp = gravityLerp;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.gravityLerp = nbtCompound.getDouble("gravityLerp");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putDouble("gravityLerp", this.gravityLerp);
    }

    public void sync() {
        InitializeComponents.EVENTS.sync(this.world);
    }

    @Override
    public void clientTick() {
        if (this.world.getRegistryKey() == World.OVERWORLD) {
            for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
                event.tick(this.world);
            }
        }
    }

    @Override
    public void serverTick() {
        if (this.world.getRegistryKey() == World.OVERWORLD) {
            for (DestructionEvent event : ServerDestructionEvent.getAllServerInstances()) {
                event.tick(this.world);
            }
        }
    }
}
