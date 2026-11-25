package com.heaper;

import com.heaper.chat.ChatPipeline;
import com.heaper.network.CameraJoltPayload;
import com.heaper.network.ImpulsePayload;
import com.heaper.network.StrainPayload;
import com.heaper.network.TetherPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandicapExtras implements ModInitializer {
	public static final String MOD_ID = "handicap_extras";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(CameraJoltPayload.TYPE, CameraJoltPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ImpulsePayload.TYPE, ImpulsePayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(TetherPayload.TYPE, TetherPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(StrainPayload.TYPE, StrainPayload.CODEC);

		ExtraHandicapsRegistry.init();
		ExtraHandicapManager.register();
		ChatPipeline.register();

		LOGGER.info("Handicap Extras loaded.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
