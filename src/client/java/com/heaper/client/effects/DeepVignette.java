package com.heaper.client.effects;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DeepVignette {
    public static final EffectChannel CHANNEL = HudEffects.register(DeepVignette::render);

    private static final float MAX_COVER = 0.55f;
    private static final float MAX_ALPHA = 0.85f;
    private static final int RGB = 0x020C1A;
    private static final int STEPS = 32;

    private DeepVignette() {}

    public static void render(GuiGraphicsExtractor g, float intensity) {
        int w = g.guiWidth();
        int h = g.guiHeight();
        int cover = Math.round(h * MAX_COVER * intensity);
        if (cover <= 0) {
            return;
        }

        for (int i = 0; i < STEPS; i++) {
            int y1 = h - cover * i / STEPS;
            int y0 = h - cover * (i + 1) / STEPS;
            if (y1 < y0) {
                continue;
            }
            float fade = 1f - (i + 0.5f) / STEPS;
            int alpha = Math.round(255 * MAX_ALPHA * intensity * fade);
            g.fill(0, y0, w, y1, (alpha << 24) | RGB);
        }
    }
}
