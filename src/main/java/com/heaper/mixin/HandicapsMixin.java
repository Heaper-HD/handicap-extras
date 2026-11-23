package com.heaper.mixin;

import com.handicap.Handicap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Handicap.class)
public enum HandicapsMixin {
    SEPARATION_ANXIETY("separation_anxiety", "Separation Anxiety", "Get slower and weaker when no one is around.");

    @Shadow
    HandicapsMixin(String id, String displayName, String description) {
    }
}
