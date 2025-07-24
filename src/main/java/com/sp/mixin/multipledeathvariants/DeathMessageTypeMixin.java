package com.sp.mixin.multipledeathvariants;

import com.mojang.serialization.Codec;
import com.sp.entity.ModDamageSources;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(DeathMessageType.class)
@Unique
public class DeathMessageTypeMixin {

    @Shadow
    @Final
    @Mutable
    private static DeathMessageType[] field_42366;


    private static final DeathMessageType PLAY_ZONE = ModDamageSources.PLAY_ZONE_TYPE = addVariant("play_zone");

    @Shadow
    @Final
    public static Codec<DeathMessageType> CODEC = StringIdentifiable.createCodec(DeathMessageTypeMixin::getDeathMessageTypes);

    @Unique
    private static DeathMessageType[] getDeathMessageTypes() {
        return DeathMessageTypeMixin.field_42366;
    }

    @Invoker("<init>")
    public static DeathMessageType invokeInit(String par1, int par2, String par3) {
        throw new AssertionError();
    }

    private static DeathMessageType addVariant(String id) {
        ArrayList<DeathMessageType> variants = new ArrayList<>(Arrays.asList(DeathMessageTypeMixin.field_42366));
        DeathMessageType messageType = invokeInit(id, variants.get(variants.size() - 1).ordinal() + 1, id);
        variants.add(messageType);
        DeathMessageType[] newList = variants.toArray(DeathMessageTypeMixin.field_42366);
        field_42366 = newList;
        return messageType;
    }

}
