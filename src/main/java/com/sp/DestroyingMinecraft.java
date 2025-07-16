package com.sp;

import com.sp.block.ModBlocks;
import com.sp.block.entity.ModBlockEntities;
import com.sp.command.DestructionCommand;
import com.sp.command.RipPlatformOutCommand;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.server.custom.PlanetDestructionServer;
import com.sp.destruction.server.custom.SupernovaDestructionServer;
import com.sp.entity.ModEntities;
import com.sp.item.ModItemGroups;
import com.sp.item.ModItems;
import com.sp.networking.InitializePackets;
import com.sp.networking.S2C.InvokeDestructionPacket;
import com.sp.networking.S2C.PointSBEPacket;
import com.sp.render.camerashake.AbstractCameraShakeInstance;
import com.sp.sounds.ModSounds;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyingMinecraft implements ModInitializer {
	public static final String MOD_ID = "destroying-minecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final PlanetDestructionServer planetServerDestruction = new PlanetDestructionServer();
	public static final SupernovaDestructionServer supernovaServerDestruction = new SupernovaDestructionServer();

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.init();
		ModItemGroups.registerItemGroups();
		InitializePackets.registerServerNetworking();
		ModEntities.registerEntities();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		MidnightConfig.init(MOD_ID, DestroyingMinecraftConfig.class);

		CommandRegistrationCallback.EVENT.register(DestructionCommand::register);
		CommandRegistrationCallback.EVENT.register(RipPlatformOutCommand::register);

		LOGGER.info("\"It's nukein' time\" -He said as he loaded the fork into the microwave");

		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			for(SpinningBlockExplosion explosion : SpinningBlockExplosion.getExplosions()){
				explosion.explode(serverWorld);
			}

			BlackHoleDestruction.tick(serverWorld);
		});
	}

	public static void sendPointSBEPacket(PlayerEntity player, Vec3d position, int radius) {
		ServerPlayNetworking.send((ServerPlayerEntity) player, new PointSBEPacket.SBEPayload(position.toVector3f(), radius));
	}

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}
}