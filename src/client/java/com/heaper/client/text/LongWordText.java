package com.heaper.client.text;

import com.handicap.Handicap;
import com.handicap.client.ClientState;
import com.heaper.extra_handicaps.Hippopotomonstrosesquippedaliophobia;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LongWordText {
    private static final long SHIVER_MS = 120;
    private static final Pattern LONG_WORD = Pattern.compile("(?<!\u00a7)"
            + Hippopotomonstrosesquippedaliophobia.LONG_WORD.pattern());

    private static final Map<String, String> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<String, String>(256, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > 1024;
                }
            });

    private static Handicap handicap;
    private static long cachedBucket = -1;

    private LongWordText() {}

    public static boolean active() {
        if (handicap == null) {
            handicap = Handicap.byId("hippopotomonstrosesquippedaliophobia");
        }
        return handicap != null && TextEffects.allowed() && ClientState.has(handicap);
    }

    public static String apply(String text) {
        if (text == null || text.length() < 12) {
            return text;
        }

        if (!active() || !LONG_WORD.matcher(text).find()) {
            return text;
        }

        long bucket = System.currentTimeMillis() / SHIVER_MS;
        synchronized (CACHE) {
            if (bucket != cachedBucket) {
                CACHE.clear();
                cachedBucket = bucket;
            }
            return CACHE.computeIfAbsent(text, t -> shiver(t, bucket));
        }
    }

    private static String shiver(String text, long bucket) {
        Matcher m = LONG_WORD.matcher(text);
        StringBuilder out = new StringBuilder(text.length());
        Random rnd = new Random(text.hashCode() * 31L + bucket);
        while (m.find()) {
            StringBuilder word = new StringBuilder(m.group().length());
            for (char c : m.group().toCharArray()) {
                char r = (char) ('a' + rnd.nextInt(26));
                word.append(Character.isUpperCase(c) ? Character.toUpperCase(r) : r);
            }
            m.appendReplacement(out, word.toString());
        }
        m.appendTail(out);
        return out.toString();
    }
}
