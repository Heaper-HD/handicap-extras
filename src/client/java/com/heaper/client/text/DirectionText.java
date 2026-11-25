package com.heaper.client.text;

import com.handicap.Handicap;
import com.handicap.client.ClientState;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectionText {
    public static final long SHUFFLE_MS = 30_000;
    private static final Pattern WORD = Pattern.compile("(?i)\\b(north|east|south|west|positive|negative)\\b");

    private static Handicap handicap;
    private static long mappingBucket = -1;
    private static final Map<String, String> MAPPING = new HashMap<>();

    private DirectionText() {}

    public static boolean active() {
        if (handicap == null) {
            handicap = Handicap.byId("no_sense_of_direction");
        }
        return handicap != null && TextEffects.allowed() && ClientState.has(handicap);
    }

    public static String apply(String text) {
        if (text == null || text.length() < 4 || !active()) {
            return text;
        }
        Matcher m = WORD.matcher(text);
        if (!m.find()) {
            return text;
        }
        Map<String, String> mapping = mapping();
        m.reset();
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String word = m.group();
            String swapped = mapping.get(word.toLowerCase(Locale.ROOT));
            if (Character.isUpperCase(word.charAt(0))) {
                swapped = Character.toUpperCase(swapped.charAt(0)) + swapped.substring(1);
            }
            m.appendReplacement(out, swapped);
        }
        m.appendTail(out);
        return out.toString();
    }

    private static synchronized Map<String, String> mapping() {
        long bucket = System.currentTimeMillis() / SHUFFLE_MS;
        if (bucket != mappingBucket) {
            mappingBucket = bucket;
            Random rnd = new Random(bucket);

            boolean swapNS = rnd.nextBoolean();
            boolean swapEW = rnd.nextBoolean();
            if (!swapNS && !swapEW) {
                swapNS = true;
            }
            boolean flipSign = rnd.nextBoolean();

            MAPPING.clear();
            MAPPING.put("north", swapNS ? "south" : "north");
            MAPPING.put("south", swapNS ? "north" : "south");
            MAPPING.put("east", swapEW ? "west" : "east");
            MAPPING.put("west", swapEW ? "east" : "west");
            MAPPING.put("positive", flipSign ? "negative" : "positive");
            MAPPING.put("negative", flipSign ? "positive" : "negative");
        }
        return MAPPING;
    }

    public static float compassOffset() {
        long now = System.currentTimeMillis();
        float base = new Random(now / SHUFFLE_MS).nextFloat();
        float wobble = (float) Math.sin(now / 1700.0) * 0.04f;
        return base + wobble;
    }
}
