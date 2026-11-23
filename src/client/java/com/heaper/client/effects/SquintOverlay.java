package com.heaper.client.effects;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SquintOverlay {
    public static final EffectChannel CHANNEL = new EffectChannel();

    private static final float MAX_COVER = 0.38f;
    private static final float SOLID_PART = 0.55f;
    private static final int FADE_STEPS = 24;

    private SquintOverlay() {}

    public static void render(GuiGraphicsExtractor g) {
        float intensity = CHANNEL.current();
        if (intensity <= 0f) {
            return;
        }

        int w = g.guiWidth();
        int h = g.guiHeight();
        int bar = Math.round(h * MAX_COVER * intensity);
        if (bar <= 0) {
            return;
        }

        int solid = Math.round(bar * SOLID_PART);
        int fade = bar - solid;

        g.fill(0, 0, w, solid, 0xFF000000);
        g.fill(0, h - solid, w, h, 0xFF000000);

        for (int i = 0; i < FADE_STEPS; i++) {
            int y0 = solid + fade * i / FADE_STEPS;
            int y1 = solid + fade * (i + 1) / FADE_STEPS;
            if (y1 <= y0) {
                continue;
            }
            int alpha = Math.round(255 * (1f - (i + 0.5f) / FADE_STEPS));
            int color = alpha << 24;
            g.fill(0, y0, w, y1, color);
            g.fill(0, h - y1, w, h -y0, color);
        }
    }
}
