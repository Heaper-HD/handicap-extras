package com.heaper.client.effects;

import com.heaper.HandicapExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SilhouetteOverlay {
    public static final EffectChannel CHANNEL = HudEffects.register(SilhouetteOverlay::render);

    private static final Identifier LEFT = HandicapExtras.id("textures/gui/thalassophobia/silhouette_left.png");
    private static final Identifier RIGHT = HandicapExtras.id("textures/gui/thalassophobia/silhouette_right.png");

    private static final float THRESHOLD = 0.6f;
    private static final float SPAWN_CHANCE = 0.02f;
    private static final int MAX_SHADOWS = 2;
    private static final float MAX_ALPHA = 0.45f;

    private record Shadow(long start, long duration, float y, float scale, boolean right) {}

    private static final List<Shadow> SHADOWS = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private SilhouetteOverlay() {}

    public static void tick(float intensity) {
        float strength = (intensity - THRESHOLD) / (1f - THRESHOLD);
        if (strength <= 0f || SHADOWS.size() >= MAX_SHADOWS) {
            return;
        }
        if (RANDOM.nextFloat() < SPAWN_CHANCE * strength) {
            SHADOWS.add(new Shadow(
                    System.currentTimeMillis(),
                    6000 + RANDOM.nextInt(6000),
                    0.55f + RANDOM.nextFloat() * 0.35f,
                    0.8f + RANDOM.nextFloat() * 1.4f,
                    RANDOM.nextBoolean()));
        }
    }

    public static void clear() {
        SHADOWS.clear();
    }

    public static void render(GuiGraphicsExtractor g, float intensity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || SHADOWS.isEmpty()) {
            return;
        }

        float lookDown = Mth.clamp((mc.player.getXRot() - 20f) / 40f, 0f, 1f);
        long now = System.currentTimeMillis();
        int w = g.guiWidth();
        int h = g.guiHeight();

        Iterator<Shadow> it = SHADOWS.iterator();
        while (it.hasNext()) {
            Shadow s = it.next();
            float t = (now - s.start()) / (float) s.duration();
            if (t >= 1f) {
                it.remove();
                continue;
            }
            if (lookDown <= 0f) {
                continue;
            }

            int sw = Math.round(128 * s.scale());
            int sh = Math.round(48 * s.scale());
            float travel = s.right() ? t : 1f - t;
            int x = Math.round(Mth.lerp(travel, -sw, w));
            int y = Math.round(h * s.y()) - sh / 2;

            float alpha = Mth.sin(t * Mth.PI) * MAX_ALPHA * lookDown;
            int color = (Math.round(255 * alpha) << 24) | 0x000000;
            g.blit(RenderPipelines.GUI_TEXTURED, s.right() ? RIGHT : LEFT,
                    x, y, 0f, 0f, sw, sh, 256, 96, 256, 96, color);
        }
    }
}
