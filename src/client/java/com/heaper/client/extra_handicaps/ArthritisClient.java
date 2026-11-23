package com.heaper.client.extra_handicaps;

import com.heaper.client.ExtraClientHandicap;
import com.heaper.extra_handicaps.Arthritis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

public class ArthritisClient extends ExtraClientHandicap {
    public ArthritisClient() {
        super("arthritis");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player != null && mc.gui.screen() instanceof AbstractContainerScreen<?> && Arthritis.locked(mc.player)) {
            mc.gui.setScreen(null);
            mc.player.sendOverlayMessage(Component.literal("Your joints hurt too much..."));
        }
    }
}
