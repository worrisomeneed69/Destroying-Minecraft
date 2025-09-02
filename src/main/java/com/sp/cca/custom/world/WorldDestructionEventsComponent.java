package com.sp.cca.custom.world;

import com.sp.cca.InitializeComponents;
import com.sp.destruction.DestructionEvent;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.Vector;

public class WorldDestructionEventsComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final World world;
    private boolean syncLight;
    private DestructionEvent currentDestructionEvent;
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

    public DestructionEvent getCurrentDestructionEvent() {
        return this.currentDestructionEvent;
    }
    public void setAndStartCurrentDestructionEvent(DestructionEvent currentDestructionEvent, long startTime) {
        if (this.currentDestructionEvent != null) {
            this.currentDestructionEvent.setActive(false, -1);
//            this.currentDestructionEvent.resetEvent();
        }
        this.currentDestructionEvent = currentDestructionEvent;
        this.currentDestructionEvent.setActive(true, startTime);
    }


    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        if (this.syncLight) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putDouble("gravityLerp", this.gravityLerp);
            nbtCompound.putDouble("currentDestructionEventProgress", this.currentDestructionEvent != null ? this.currentDestructionEvent.getProgress() : -1);

            buf.writeNbt(nbtCompound);
            this.syncLight = false;
        }
        AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.gravityLerp = nbtCompound.getDouble("gravityLerp");
        if (this.currentDestructionEvent != null) {
            this.currentDestructionEvent.setProgress(nbtCompound.getInt("currentDestructionEventProgress"));
        }

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
        nbtCompound.putDouble("currentDestructionEventProgress", this.currentDestructionEvent != null ? this.currentDestructionEvent.getProgress() : -1);

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

    public void syncLight() {
        this.syncLight = true;
        this.sync();
    }

    public void sync() {
        InitializeComponents.EVENTS.sync(this.world);
    }

//    @Override
//    public void clientTick() {
//        if (this.world.getRegistryKey() == World.OVERWORLD) {
//            for (DestructionEvent event : ClientDestructionEvent.getAllClientInstances()) {
//                event.tick(this.world);
//            }
//        }
//    }

//    @Override
//    public void serverTick() {
//        if (this.world.getRegistryKey() == World.OVERWORLD) {
//            for (DestructionEvent event : ServerDestructionEvent.getAllServerInstances()) {
//                event.tick(this.world);
//            }
//        }
//    }

    @Override
    public void tick() {
        if (this.world.getRegistryKey() == World.OVERWORLD && this.currentDestructionEvent != null) {
            this.currentDestructionEvent.tick(this.world);
        }
    }
}
