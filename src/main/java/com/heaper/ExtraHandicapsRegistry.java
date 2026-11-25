package com.heaper;

import com.handicap.Handicap;
import com.heaper.extra_handicaps.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtraHandicapsRegistry {
    private static final Map<Handicap, ExtraHandicap> HANDICAPS = new LinkedHashMap<>();
    private static boolean frozen = false;

    public static void register(ExtraHandicap h) {
        if (frozen) {
            throw new IllegalStateException("Handicap registry is frozen");
        }

        if (HANDICAPS.containsKey(h.handicap())) {
            throw new IllegalStateException("Handicap '" + h.id() + "' was already registered.");
        }

        HANDICAPS.put(h.handicap(), h);
        h.registerEvents();
    }

    public static ExtraHandicap get(Handicap h) {
        return HANDICAPS.get(h);
    }

    public static Collection<ExtraHandicap> all() {
        return Collections.unmodifiableCollection(HANDICAPS.values());
    }

    public static void init() {
        register(new Arthritis());
        register(new Narcolepsy());

        register(new SeparationAnxiety());
        register(new Gigantism());
        register(new MotionSickness());
        register(new Agoraphobia());
        register(new Nyctophobia());
        register(new SunAllergy());
        register(new Acrophobia());
        register(new Aquaphobia());
        register(new Hyperphagia());
        register(new Photophobia());
        register(new Pyrophobia());
        register(new Hemophilia());
        register(new PickyEater());
        register(new SocialAnxiety());
        register(new Hemophobia());
        register(new LoudSnoring());
        register(new Allergy());
        register(new Stutter());
        register(new ForeignAccent());
        register(new Echolalia());
        register(new Aphasia());
        register(new BrokenTelephone());
        register(new CompulsiveGifting());
        register(new SympatheticPain());
        register(new CantFeedYourself());
        register(new Hypothermia());
        register(new Germaphobia());
        register(new Featherweight());
        register(new Heavyweight());
        register(new ContagiousCold());
        register(new Conjoined());
        register(new Hippopotomonstrosesquippedaliophobia());
        register(new BadBack());
        register(new NightTerrors());
        register(new StageFright());
        register(new Paranoia());
        register(new SelectiveMutism());
        register(new Anemia());
        register(new HeavyHanded());
        register(new HeavySleeper());
        register(new HoardingDisorder());
        register(new SleepTalking());
        register(new Pica());
        register(new Amnesia());

        frozen = true;
    }
}
