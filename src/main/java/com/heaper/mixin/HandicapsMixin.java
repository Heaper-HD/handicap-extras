package com.heaper.mixin;

import com.handicap.Handicap;
import com.heaper.extra_handicaps.Pyrophobia;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Handicap.class)
public enum HandicapsMixin {
    SEPARATION_ANXIETY("separation_anxiety", "Separation Anxiety", "Get slower and weaker when no one is around"),
    GIGANTISM("gigantism", "Gigantism", "You are huge: more reach, but you don't fit trough normal doors"),
    MOTION_SICKNESS("motion_sickness", "Motion Sickness", "Riding anything makes you nauseous and weak"),
    AGARAPHOBIA("agoraphobia", "Agoraphobia", "Being under the open sky slows you down and makes you tremble"),
    NYCTOPHOBIA("nyctophobia", "Nyctophobia", "Darkness weakens and slows you unless you hold a light"),
    SUN_ALLERGY("sun_allergy", "Sun Allergy", "Direct sunlight sets you on fire unless you wear a helmet"),
    ACROPHOBIA("acrophobia", "Acrophobia", "Standing near a big drop makes you tremble and slows you down"),
    AQUAPHOBIA("aquaphobia", "Aquaphobia", "Water makes you panic: you swim slower and hit weaker"),
    HYPERPHAGIA("hyperphagia", "Hyperphagia", "You get hungry much faster"),
    PHOTOPHOBIA("photophobia", "Photophobia", "Bright lights make you squint: slower and weaker unless you wear a helmet"),
    PYROPHOBIA("pyrophobia", "Pyrophobia", "Being near fire or lava makes you panic");

    @Shadow
    HandicapsMixin(String id, String displayName, String description) {
    }
}
