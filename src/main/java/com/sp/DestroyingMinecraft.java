package com.sp;

import com.sp.block.ModBlocks;
import com.sp.command.DestructionCommand;
import com.sp.command.RipPlatformOutCommand;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.entity.ModEntities;
import com.sp.item.ModItemGroups;
import com.sp.item.ModItems;
import com.sp.networking.InitializePackets;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyingMinecraft implements ModInitializer {
	public static final String MOD_ID = "destroying-minecraft";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.init();
		ModItemGroups.registerItemGroups();
		InitializePackets.registerServerNetworking();
		ModEntities.init();
		MidnightConfig.init(MOD_ID, DestroyingMinecraftConfig.class);

		CommandRegistrationCallback.EVENT.register(DestructionCommand::register);
		CommandRegistrationCallback.EVENT.register(RipPlatformOutCommand::register);

		LOGGER.info("\"It's nukein' time\" -I say as I load a few grapes into the microwave");

		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			for(SpinningBlockExplosion explosion : SpinningBlockExplosion.getExplosions()){
				explosion.explode(serverWorld);
			}
		});
	}

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}
}