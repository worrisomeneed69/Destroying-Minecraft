package com.sp;

import com.sp.block.ModBlocks;
import com.sp.block.entity.ModBlockEntities;
import com.sp.command.*;
import com.sp.component.ModDataComponentTypes;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.server.custom.LaserDestructionServer;
import com.sp.destruction.server.custom.PlanetDestructionServer;
import com.sp.destruction.server.custom.SupernovaDestructionServer;
import com.sp.destruction.server.custom.blackhole.BlackHoleDestructionServerPart1;
import com.sp.destruction.server.custom.blackhole.BlackHoleDestructionServerPart2;
import com.sp.entity.ModEntities;
import com.sp.item.ModItemGroups;
import com.sp.item.ModItems;
import com.sp.networking.InitializePackets;
import com.sp.networking.S2C.BraamPacket;
import com.sp.networking.S2C.PointSBEPacket;
import com.sp.networking.S2C.UpdatePlayZonePacket;
import com.sp.networking.S2C.WaitingRoomPacket;
import com.sp.sounds.ModSounds;
import com.sp.world.ModGameRules;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.playzone.PlayZone;
import com.sp.world.playzone.PlayZoneManager;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyingMinecraft implements ModInitializer {
	public static final String MOD_ID = "destroying-minecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final PlanetDestructionServer planetServerDestruction = new PlanetDestructionServer();
	public static final SupernovaDestructionServer supernovaServerDestruction = new SupernovaDestructionServer();
	public static final BlackHoleDestructionServerPart1 blackHoleDestructionPart1 = new BlackHoleDestructionServerPart1();
	public static final BlackHoleDestructionServerPart2 blackHoleDestructionPart2 = new BlackHoleDestructionServerPart2();
	public static final LaserDestructionServer laserDestruction = new LaserDestructionServer();

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.init();
		ModItemGroups.registerItemGroups();
		InitializePackets.registerServerNetworking();
		ModEntities.registerEntities();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		ModGameRules.registerGameRules();
		ModDataComponentTypes.registerDataComponentTypes();

		MidnightConfig.init(MOD_ID, DestroyingMinecraftConfig.class);

		CommandRegistrationCallback.EVENT.register(DestructionCommand::register);
		CommandRegistrationCallback.EVENT.register(RipPlatformOutCommand::register);
		CommandRegistrationCallback.EVENT.register(AddPlayZoneCommand::register);
        CommandRegistrationCallback.EVENT.register(RevealBlackHoleCommand::register);
        CommandRegistrationCallback.EVENT.register(WaitingRoomCommand::register);

		LOGGER.info("\"It's nukein' time\" -He said as he loaded the fork into the microwave");

		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			for(SpinningBlockExplosion explosion : SpinningBlockExplosion.getExplosions()){
				explosion.explode(serverWorld);
			}

			BlackHoleDestruction.tick(serverWorld);
		});

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            System.out.println("SERVER CLEARED=======================================");
            PlayZoneManager.clearAllPlayZones();
        });
	}

	public static Vec3d getGravityDir() {
		return new Vec3d(0.0, 0.07, -0.03);
	}

	public static void sendPointSBEPacket(PlayerEntity player, Vec3d position, int radius) {
		ServerPlayNetworking.send((ServerPlayerEntity) player, new PointSBEPacket.SBEPayload(position.toVector3f(), radius));
	}

	public static void sendBraamPacket(PlayerEntity player, SoundEvent soundEvent) {
		ServerPlayNetworking.send((ServerPlayerEntity) player, new BraamPacket.BraamPayload(soundEvent));
	}

	public static void sendUpdatePlayZonePacket(PlayerEntity player, PlayZone playZone, boolean remove) {
		Box playZoneBounds = playZone.getBoundingBox();
		ServerPlayNetworking.send((ServerPlayerEntity) player, new UpdatePlayZonePacket.UpdatePlayZonePayload(
				playZoneBounds.minX,
				playZoneBounds.maxX,
				playZoneBounds.minY,
				playZoneBounds.maxY,
				playZoneBounds.minZ,
				playZoneBounds.maxZ,
				playZone.getId(),
				remove
		));
	}

    public static void sendWaitingRoomPacket(PlayerEntity player, boolean setInWaitingRoom) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new WaitingRoomPacket.WaitingRoomPacketPayload(setInWaitingRoom));
    }

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}
}