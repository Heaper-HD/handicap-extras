package com.heaper.client;

import com.heaper.client.effects.HudEffects;
import net.fabricmc.api.ClientModInitializer;

public class HandicapExtrasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ExtraClientHandicapsRegistry.init();
		ExtraClientHandicapManager.register();
		HudEffects.init();
	}
}