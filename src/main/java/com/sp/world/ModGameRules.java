package com.sp.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_EXPLOSIONS =
            GameRuleRegistry.register("allowExplosions", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static void registerGameRules() {

    }

}
