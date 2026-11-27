package com.heaper.client.effects;

import com.heaper.HandicapExtras;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class SynesthesiaOverlay {
    public static final EffectChannel CHANNEL = HudEffects.register(SynesthesiaOverlay::render);

    public static final boolean FLASH_SAFE = true;

    public enum Kind {
        HOSTILE("spike", 0xE53935),
        NEUTRAL("blob", 0x43A047),
        BLOCKS("square", 0xFB8C00),
        AMBIENT("wave", 0x1E88E5),
        PLAYERS("triangle", 0xFDD835);

        final Identifier texture;
        final int rgb;

        Kind(String texture, int rgb) {
            this.texture = HandicapExtras.id("textures/gui/synesthesia/" + texture + ".png");
            this.rgb = rgb;
        }
    }

    private static final int TEX = 64;
    private static final int MAX_SHAPES = 40;
    private static final long FLASH_MS = 400;

    private record Shape(Kind kind, float fx, float fy, float size, float driftX, float driftY,
                         float alpha, long start, long life) {}

    private static final Deque<Shape> SHAPES = new ArrayDeque<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private static float flashPeak = 0f;
    private static long flashStart = 0;

    private SynesthesiaOverlay() {}

    public static void spawn(Kind kind, float relAngle, float loudness, float pitch) {
        float radius = Mth.lerp(loudness, 0.45f, 0.08f);
        float rad = relAngle * Mth.DEG_TO_RAD;
        float fx = 0.5f + Mth.sin(rad) * radius + (RANDOM.nextFloat() - 0.5f) * 0.06f;
        float fy = 0.5f - Mth.cos(rad) * radius + (RANDOM.nextFloat() - 0.5f) * 0.06f;
        float size = Mth.lerp(loudness, 14f, 56f) * Mth.clamp(1f / Math.max(pitch, 0.1f), 0.5f, 2f);

        if (SHAPES.size() >= MAX_SHAPES) {
            SHAPES.pollFirst();
        }
        SHAPES.addLast(new Shape(kind, fx, fy, size,
                (RANDOM.nextFloat() - 0.5f) * 30f,
                (RANDOM.nextFloat() - 0.5f) * 30f - 10f,
                Mth.lerp(loudness, 0.35f, 0.7f),
                System.currentTimeMillis(), 900 + RANDOM.nextInt(700)));
    }

    public static void flash(float strength) {
        long now = System.currentTimeMillis();
        if (FLASH_SAFE) {
            if (now - flashStart < 350) {
                return;
            }
            strength = Math.min(strength, 0.3f);
        }
        flashPeak = strength;
        flashStart = now;
    }

    public static void clear() {
        SHAPES.clear();
        flashPeak = 0f;
    }

    public static void render(GuiGraphicsExtractor g, float intensity) {
        long now = System.currentTimeMillis();
        int w = g.guiWidth();
        int h = g.guiHeight();

        float flash = flashPeak * (1f - (now - flashStart) / (float) FLASH_MS);
        if (flash > 0f) {
            g.fill(0, 0, w, h, (Math.round(255 * flash * intensity) << 24) | 0xFFFFFF);
        }

        Iterator<Shape> it = SHAPES.iterator();
        while (it.hasNext()) {
            Shape s = it.next();
            float t = (now - s.start()) / (float) s.life();
            if (t >= 1f) {
                it.remove();
                continue;
            }

            float fadeIn = Math.min(1f, t * 6f);
            float alpha = s.alpha() * fadeIn * (1f - t) * intensity;
            int size = Math.round(s.size() * (1f + 0.8f * t));
            int cx = Math.round(s.fx() * w + s.driftX() * t);
            int cy = Math.round(s.fy() * h + s.driftY() * t);
            int color = (Math.round(255 * alpha) << 24) | s.kind().rgb;

            g.blit(RenderPipelines.GUI_TEXTURED, s.kind().texture,
                    cx - size / 2, cy - size / 2, 0f, 0f, size, size, TEX, TEX, TEX, TEX, color);
        }
    }
}
