package com.heaper.client.effects;

import com.heaper.HandicapExtras;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class HudEffects {
    private record Entry(EffectChannel channel, HudEffect effect) {}

    private static final List<Entry> EFFECTS = new ArrayList<>();

    private HudEffects() {}

    public static EffectChannel register(HudEffect effect) {
        EffectChannel channel = new EffectChannel();
        EFFECTS.add(new Entry(channel, effect));
        return channel;
    }

    public static void init() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, HandicapExtras.id("effects"),
                (graphics, tickCounter) -> render(graphics));
    }

    private static void render(GuiGraphicsExtractor g) {
        for (Entry entry : EFFECTS) {
            float intensity = entry.channel().current();
            if (intensity > 0f) {
                entry.effect().render(g, intensity);
            }
        }
    }
}
