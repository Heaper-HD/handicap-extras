package com.heaper.client.text;

import com.heaper.HandicapExtras;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class TextRefresher {
    private static boolean altered = false;

    private TextRefresher() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            boolean now = mc.player != null && (MirrorText.active() || LongWordText.active()
                    || DyscalculiaText.active() || DirectionText.active());
            if (now == altered) {
                return;
            }
            altered = now;
            HandicapExtras.LOGGER.info("Text refresh: reloading (altered={})", now);

            // 1. Invalidate every cached text layout (item names, static labels)
            mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
            // 2. Re-wrap chat history, which caches its own lines
            mc.gui.hud.getChat().rescaleChat();
        });
    }
}
