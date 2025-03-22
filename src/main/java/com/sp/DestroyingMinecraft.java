package com.sp;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyingMinecraft implements ModInitializer {
	public static final String MOD_ID = "destroying-minecraft";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}
}