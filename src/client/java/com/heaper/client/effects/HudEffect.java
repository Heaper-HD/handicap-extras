package com.heaper.client.effects;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@FunctionalInterface
public interface HudEffect {
    void render(GuiGraphicsExtractor g, float intensity);
}
