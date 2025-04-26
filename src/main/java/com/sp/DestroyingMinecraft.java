package com.sp;

import com.sp.block.ModBlocks;
import com.sp.command.DestructionCommand;
import com.sp.entity.ModEntities;
import com.sp.item.ModItemGroups;
import com.sp.item.ModItems;
import com.sp.networking.InitializePackets;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
		ModEntities.registerEntities();

		CommandRegistrationCallback.EVENT.register(DestructionCommand::register);

		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}

	public static void sendSupernovaPacket(){

	}
}