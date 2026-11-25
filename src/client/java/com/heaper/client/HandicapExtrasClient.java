package com.heaper.client;

import com.heaper.client.effects.HudEffects;
import com.heaper.client.rope.TetherClient;
import com.heaper.network.CameraJoltPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public class HandicapExtrasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ExtraClientHandicapsRegistry.init();
		ExtraClientHandicapManager.register();
		TetherClient.register();
		HudEffects.init();

		ClientPlayNetworking.registerGlobalReceiver(CameraJoltPayload.TYPE, (payload, context) -> {
			LocalPlayer p = context.player();
			p.setXRot(Mth.clamp(p.getXRot() + payload.pitch(), -90f, 90f));
			p.setYRot(p.getYRot() + payload.yaw());
		});
	}
}