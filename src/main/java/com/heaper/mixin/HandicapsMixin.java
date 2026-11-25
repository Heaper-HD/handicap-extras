package com.heaper.mixin;

import com.handicap.Handicap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Handicap.class)
public enum HandicapsMixin {
    SEPARATION_ANXIETY("separation_anxiety", "Separation Anxiety", "Get slower and weaker when no one is around"),
    GIGANTISM("gigantism", "Gigantism", "You are huge: more reach, but you don't fit through normal doors"),
    MOTION_SICKNESS("motion_sickness", "Motion Sickness", "Riding anything makes you nauseous and weak"),
    AGORAPHOBIA("agoraphobia", "Agoraphobia", "Being under the open sky slows you down and makes you tremble"),
    NYCTOPHOBIA("nyctophobia", "Nyctophobia", "Darkness weakens and slows you unless you hold a light"),
    SUN_ALLERGY("sun_allergy", "Sun Allergy", "Direct sunlight sets you on fire unless you wear a helmet"),
    ACROPHOBIA("acrophobia", "Acrophobia", "Standing near a big drop makes you tremble and slows you down"),
    AQUAPHOBIA("aquaphobia", "Aquaphobia", "Water makes you panic: you swim slower and hit weaker"),
    HYPERPHAGIA("hyperphagia", "Hyperphagia", "You get hungry much faster"),
    PHOTOPHOBIA("photophobia", "Photophobia", "Bright lights make you squint: slower and weaker unless you wear a helmet"),
    PYROPHOBIA("pyrophobia", "Pyrophobia", "Being near fire or lava makes you panic"),
    HEMOPHILIA("hemophilia", "Hemophilia", "Getting hurt makes you bleed until you bandage yourself with paper or wool"),
    PICKY_EATER("picky_eater", "Picky Eater", "Eating the same food twice in a row barely feeds you and makes you sick"),
    SOCIAL_ANXIETY("social_anxiety", "Social Anxiety", "Being around a group of players make you slow, weak and shaky"),
    HEMOPHOBIA("hemophobia", "Hemophobia", "Seeing another player get hurt makes you faint"),
    LOUD_SNORING("loud_snoring", "Loud Snoring", "While you sleep, nobody near you can sleep"),
    ALLERGY("allergy", "Allergy", "You're allergic to one random thing: being near it makes you sneeze, and staying close poisons you"),
    STUTTER("stutter", "Stutter", "Your worlds sometimes come out stuttered"),
    FOREING_ACCENT("foreign_accent", "Foreign Accent", "Everything you say comes out in a string accent"),
    ECHOLALIA("echolalia", "Echolalia", "You sometimes repeat what others said"),
    APHASIA("aphasia", "Aphasia", "Words in your message get swapped for words others used"),
    BROKEN_TELEPHONE("broken_telephone", "Broken Telephone", "Your messages only reach one random player"),
    COMPULSIVE_GIFTING("compulsive_gifting", "Compulsive Gifting", "You sometimes throw your items at the nearest player"),
    SYMPATHETIC_PAIN("sympathetic_pain", "Sympathetic Pain", "When you see another player get hurt, you feel part of it"),
    CANT_FEED_YOURSELF("cant_feed_yourself", "Can't Feed Yourself", "You can't eat on your own; Someone has to feed you"),
    HYPORTHERMIA("hypothermia", "Hypothermia", "Could biomes freeze you unless you're near fire or a friend"),
    GERMAPHOBIA("germaphobia", "Germaphobia", "Picking up items other players dropped make you sick"),
    FEATHERWEIGHT("featherweight", "Featherweight", "Low gravity: floaty jumps that are hard to control"),
    HEAVYWEIGHT("heavyweight", "Heavyweight", "Heavy gravity: short jumps, hard falls"),
    CONTAGIOUS_CLD("contagious_cold", "Contagious Cold", "You have a cold, and it spreads to players near you"),
    CONJOINED("conjoined", "Conjoined", "You're tethered ot another player and can't get far from them"),
    DYSCALCULIA("dyscalculia", "Dyscalculia", "Every number you see is wrong, and keeps changing"),
    NO_SENSE_OF_DIRECTION("no_sense_of_direction", "No Sense of Direction", "Compasses and directions lie to you");

    @Shadow
    HandicapsMixin(String id, String displayName, String description) {
    }
}
