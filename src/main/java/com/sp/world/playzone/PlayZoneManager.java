package com.sp.world.playzone;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Vector;

public class PlayZoneManager {
    private static final Vector<PlayZone> activePlayZones = new Vector<>();
    private static boolean dirty;

    public static void addPlayZone(World world, PlayZone playZone) {
        if (!activePlayZones.stream().anyMatch(playZone1 -> playZone1.getId() == playZone.getId())) {
            activePlayZones.add(playZone);
        }

        if (!world.isClient) {
            for (PlayerEntity player : world.getPlayers()) {
                DestroyingMinecraft.sendUpdatePlayZonePacket(player, playZone, false);
            }
            InitializeComponents.EVENTS.get(world).sync();
        }
    }

    public static int removeAllPlayZonesAtPos(Vec3d pos, World world) {
        int playZonesRemoved = 0;
        for (PlayZone playZone : getActivePlayZones()) {
            if (playZone.isPositionInsideZone(pos)) {
                activePlayZones.remove(playZone);
                playZonesRemoved++;
                for (PlayerEntity player : world.getPlayers()) {
                    DestroyingMinecraft.sendUpdatePlayZonePacket(player, playZone, true);
                }
            }
        }
        InitializeComponents.EVENTS.get(world).sync();
        System.out.println(playZonesRemoved);
        return playZonesRemoved;
    }

    //Client Side
    public static void removePlayZone(int id) {
        activePlayZones.removeIf(playZone -> playZone.getId() == id);
    }

    public static void clearAllPlayZones() {
        activePlayZones.clear();
    }

    public static boolean isInsideAPlayZone(Vec3d pos) {
        return activePlayZones.isEmpty() || activePlayZones.stream().anyMatch(playZone -> playZone.isPositionInsideZone(pos));
    }

    public static Vector<PlayZone> getActivePlayZones() {
        return (Vector<PlayZone>) activePlayZones.clone();
    }

    public static void markDirty(boolean isDirty) {
        dirty = isDirty;
    }

    public static boolean isDirty() {
        return dirty;
    }
}
