package com.sp.cca.custom.world;

import com.sp.cca.InitializeComponents;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.DestructionType;
import com.sp.entity.custom.BlockPhysicsEntity;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class WorldDestructionEventsComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final World world;
    private boolean syncLight;
    private DestructionEvent currentDestructionEvent;
    private Vec3d destructionEventPosition;
    private double gravityLerp;
    public static final Vec3d gravityDir = new Vec3d(0.0, 0.07, -0.03);
//    private NbtCompound worldPlayZones;

    public WorldDestructionEventsComponent(World world) {
        this.world = world;
        this.gravityLerp = 0.0;
        this.destructionEventPosition = Vec3d.ZERO;
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
            this.currentDestructionEvent.resetEvent();

            if (this.currentDestructionEvent.getDestructionType().equals(DestructionType.BLACK_HOLE)) {
                BlackHoleDestruction.setStartDestruction(false);
                this.world.getEntitiesByClass(
                        BlockPhysicsEntity.class,
                        Box.of(this.destructionEventPosition, 1000, 1000, 1000),
                        blockPhysicsEntity -> true).forEach(Entity::discard);
                this.setGravityLerp(0.0);
                this.syncLight();
            }

        }

        if (currentDestructionEvent == null) {
            this.currentDestructionEvent = null;
            return;
        }

        if (currentDestructionEvent.isClient() != this.world.isClient) {
            throw new RuntimeException("Tried to add a " + (currentDestructionEvent.isClient() ? "client" : "server") + " event on a " + (this.world.isClient ? "client" : "server") + " world");
        }

        this.currentDestructionEvent = currentDestructionEvent;
        this.currentDestructionEvent.setActive(true, startTime);
    }

    public Vec3d getDestructionEventPosition() {
        return this.destructionEventPosition;
    }

    public void setDestructionEventPosition(Vec3d position) {
        this.destructionEventPosition = position;
    }


    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        if (this.syncLight) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putDouble("gravityLerp", this.gravityLerp);
            nbtCompound.putDouble("currentDestructionEventProgress", this.currentDestructionEvent != null ? this.currentDestructionEvent.getProgress() : -1);

            buf.writeNbt(nbtCompound);
            this.syncLight = false;
        }else {
            AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.gravityLerp = nbtCompound.getDouble("gravityLerp");
        if (this.currentDestructionEvent != null) {
            this.currentDestructionEvent.setProgress(nbtCompound.getInt("currentDestructionEventProgress"));
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putDouble("gravityLerp", this.gravityLerp);
        nbtCompound.putDouble("currentDestructionEventProgress", this.currentDestructionEvent != null ? this.currentDestructionEvent.getProgress() : -1);

    }

    public void syncLight() {
        this.syncLight = true;
        this.sync();
    }

    public void sync() {
        InitializeComponents.EVENTS.sync(this.world);
    }

    @Override
    public void tick() {
        if (this.world.getRegistryKey() == World.OVERWORLD && this.currentDestructionEvent != null) {
            this.currentDestructionEvent.tick(this.world);
        }
    }
}
