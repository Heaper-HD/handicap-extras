package com.heaper.client.text;

import net.minecraft.client.Minecraft;

public final class TextEffects {
    private static int suppress = 0;

    public static void push() { suppress++; }
    public static void pop() { suppress--; }

    public static boolean allowed() {
        Minecraft mc = Minecraft.getInstance();
        return suppress == 0
                && mc.isSameThread()
                && mc.player != null;
    }
}
