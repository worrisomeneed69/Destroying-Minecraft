package com.sp.entity;

import com.sp.DestroyingMinecraft;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class ModDamageSources {
    public static DeathMessageType PLAY_ZONE_TYPE;
    public static DeathMessageType CRACKS_TYPE;

    public static RegistryKey<DamageType> PLAY_ZONE_DAMAGE_TYPE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, DestroyingMinecraft.idOf("play_zone"));

    public static RegistryKey<DamageType> CRACKS_DAMAGE_TYPE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, DestroyingMinecraft.idOf("cracks"));


    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
