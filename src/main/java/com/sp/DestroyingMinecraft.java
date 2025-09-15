package com.sp;

import com.sp.block.ModBlocks;
import com.sp.block.entity.ModBlockEntities;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.command.*;
import com.sp.component.ModDataComponentTypes;
import com.sp.config.DestroyingMinecraftConfig;
import com.sp.destruction.DestructionEvent;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.destruction.server.custom.InitializeDestructionServer;
import com.sp.destruction.server.custom.LaserDestructionServer;
import com.sp.destruction.server.custom.PlanetDestructionServer;
import com.sp.destruction.server.custom.SupernovaDestructionServer;
import com.sp.destruction.server.custom.blackhole.BlackHoleDestructionServerPart1;
import com.sp.destruction.server.custom.blackhole.BlackHoleDestructionServerPart2;
import com.sp.entity.ModEntities;
import com.sp.item.ModItemGroups;
import com.sp.item.ModItems;
import com.sp.networking.InitializePackets;
import com.sp.networking.S2C.*;
import com.sp.sounds.ModSounds;
import com.sp.world.ModGameRules;
import com.sp.world.destructionevent.custom.BlackHoleDestruction;
import com.sp.world.playzone.PlayZoneManager;
import com.sp.world.spinningblockexplosion.SpinningBlockExplosion;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
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
    //TODO: REMOVE INIT DESTRUCTION
    public static final InitializeDestructionServer initializeDestruction = new InitializeDestructionServer();

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
        ModArgumentTypes.registerModArgumentTypes();

		MidnightConfig.init(MOD_ID, DestroyingMinecraftConfig.class);

		CommandRegistrationCallback.EVENT.register(DestructionCommand::register);
		CommandRegistrationCallback.EVENT.register(RipPlatformOutCommand::register);
		CommandRegistrationCallback.EVENT.register(AddPlayZoneCommand::register);
        CommandRegistrationCallback.EVENT.register(RevealBlackHoleCommand::register);
        CommandRegistrationCallback.EVENT.register(PlayersCommand::register);
        CommandRegistrationCallback.EVENT.register(LavaSpewCommand::register);

		LOGGER.info("\"It's nukein' time\" -He said as he loaded the fork into the microwave");

		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			for(SpinningBlockExplosion explosion : SpinningBlockExplosion.getExplosions()) {
                if (serverWorld.equals(explosion.getWorld())) {
                    explosion.explode();
                }
			}

            if (serverWorld.getRegistryKey() == World.OVERWORLD) {
                BlackHoleDestruction.tick(serverWorld);
            }
		});

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlayZoneManager.clearAllPlayZones();

            for (DestructionEvent event : ServerDestructionEvent.getAllServerInstances()) {
                event.resetAnimations();
            }

            for (World world : server.getWorlds()) {
                WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(world);

                if (component.getCurrentDestructionEvent() != null) {
                    component.getCurrentDestructionEvent().resetEvent();
                }
                component.setAndStartCurrentDestructionEvent(null, -1);
            }
        });
	}

	public static Identifier idOf(String path){
		return Identifier.of(MOD_ID, path);
	}
}