package com.sp.cca.custom.world;

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

    public WorldDestructionEventsComponent(World world) {
        this.world = world;
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

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
