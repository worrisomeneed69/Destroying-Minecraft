package com.sp.cca.custom.world;

import com.sp.cca.InitializeComponents;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Vector;

public class WorldDestructionEventsComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    private final World world;
    private double gravityLerp;
    private NbtCompound worldPlayZones;

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

//    public NbtElement getWorldPlayZones() {
//        return worldPlayZones;
//    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.gravityLerp = nbtCompound.getDouble("gravityLerp");

        if (nbtCompound.contains("playZones")) {
            this.worldPlayZones = nbtCompound.getCompound("playZones");
            NbtCompound playZoneCompound = this.worldPlayZones;
            PlayZoneManager.clearAllPlayZones();

            for (int i = 0; i < playZoneCompound.getInt("numOfPlayZones"); i++) {
                Box playZoneBoundingBox = new Box(
                        playZoneCompound.getDouble("minX" + i),
                        playZoneCompound.getDouble("minY" + i),
                        playZoneCompound.getDouble("minZ" + i),

                        playZoneCompound.getDouble("maxX" + i),
                        playZoneCompound.getDouble("maxY" + i),
                        playZoneCompound.getDouble("maxZ" + i)
                );

                PlayZone playZone = new PlayZone(playZoneBoundingBox, playZoneCompound.getInt("id" + i));
                PlayZoneManager.addPlayZone(this.world, playZone);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putDouble("gravityLerp", this.gravityLerp);

        NbtCompound playZoneNbt = new NbtCompound();
        nbtCompound.put("playZones", playZoneNbt);
        Vector<PlayZone> activePlayZones = PlayZoneManager.getActivePlayZones();
        playZoneNbt.putInt("numOfPlayZones", activePlayZones.size());

        for (int i = 0; i < activePlayZones.size(); i++) {
            PlayZone playZone = activePlayZones.get(i);
            Box playZoneBounds = playZone.getBoundingBox();

            playZoneNbt.putDouble("minX" + i, playZoneBounds.minX);
            playZoneNbt.putDouble("minY" + i, playZoneBounds.minY);
            playZoneNbt.putDouble("minZ" + i, playZoneBounds.minZ);

            playZoneNbt.putDouble("maxX" + i, playZoneBounds.maxX);
            playZoneNbt.putDouble("maxY" + i, playZoneBounds.maxY);
            playZoneNbt.putDouble("maxZ" + i, playZoneBounds.maxZ);
            playZoneNbt.putInt("id" + i, playZone.getId());
        }

        this.worldPlayZones = playZoneNbt;
        nbtCompound.put("playZones", this.worldPlayZones);

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
