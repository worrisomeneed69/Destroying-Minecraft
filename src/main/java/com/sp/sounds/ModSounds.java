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


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = DestroyingMinecraft.idOf(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        DestroyingMinecraft.LOGGER.info("Registering Sounds for" + DestroyingMinecraft.MOD_ID);
    }

}
