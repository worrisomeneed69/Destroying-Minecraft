package com.sp.sounds;

import com.sp.DestroyingMinecraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent DOOR_OPEN = registerSoundEvent("door_open");
    public static final SoundEvent DOOR_OPENING_LOOP = registerSoundEvent("door_opening_loop");
    public static final SoundEvent DOOR_CLOSE = registerSoundEvent("door_close");

    public static final SoundEvent ORBITAL_LASER_INITIALIZE = registerSoundEvent("ol_initialize");
    public static final SoundEvent PLANET_INITIALIZE = registerSoundEvent("p_initialize");
    public static final SoundEvent SUPERNOVA_INITIALIZE = registerSoundEvent("sn_initialize");
    public static final SoundEvent BLACK_HOLE_INITIALIZE = registerSoundEvent("bh_initialize");

    public static final SoundEvent PLANET_AMBIENCE = registerSoundEvent("planet_ambience");
    public static final SoundEvent PLANET_RUMBLE = registerSoundEvent("planet_rumble");
    public static final SoundEvent PLANET_IMPACT_INITIAL = registerSoundEvent("planet_impact_initial");
    public static final SoundEvent PLANET_IMPACT = registerSoundEvent("planet_impact");

    public static final SoundEvent LASER_CHARGE = registerSoundEvent("laser_charge");
    public static final SoundEvent LASER_FIRE = registerSoundEvent("laser_fire");
    public static final SoundEvent LASER_POWER_DOWN = registerSoundEvent("laser_power_down");
    public static final SoundEvent LASER_PAUSE = registerSoundEvent("laser_pause");

    public static final SoundEvent SUPERNOVA_EXPLOSION = registerSoundEvent("supernova_explosion");

    public static final SoundEvent METEOR_WHISTLE = registerSoundEvent("meteor_whistle");
    public static final SoundEvent METEOR_IMPACT = registerSoundEvent("meteor_impact");

    public static final SoundEvent BLACK_HOLE_AMBIENCE = registerSoundEvent("black_hole_ambience");
    public static final SoundEvent BLACK_HOLE_DESTRUCTION_AMBIENCE = registerSoundEvent("black_hole_destruction_ambience");

    public static final SoundEvent SNAP_SNAP = registerSoundEvent("snap_snap");
    public static final SoundEvent SNAP_SNAP_RUMBLE = registerSoundEvent("snap_snap_rumble");
    public static final SoundEvent SNAP_SNAP_BREAK_OFF = registerSoundEvent("snap_snap_break_off");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = DestroyingMinecraft.idOf(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        DestroyingMinecraft.LOGGER.info("Registering Sounds for" + DestroyingMinecraft.MOD_ID);
    }

}
