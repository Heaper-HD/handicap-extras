package com.heaper.client;

import net.fabricmc.api.ClientModInitializer;

public class HandicapExtrasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ExtraClientHandicapsRegistry.init();
		ExtraClientHandicapManager.register();
	}
}