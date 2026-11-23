package com.heaper.client;

import com.heaper.HandicapExtras;
import com.heaper.client.effects.SquintOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class HandicapExtrasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ExtraClientHandicapsRegistry.init();
		ExtraClientHandicapManager.register();
		HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, HandicapExtras.id("squint"),
				((graphics, tickCounter) -> SquintOverlay.render(graphics)));
	}
}