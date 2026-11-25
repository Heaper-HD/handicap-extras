package com.heaper.text;

import com.handicap.Handicap;
import com.handicap.client.ClientState;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DyscalculiaText {
    private static final long SHUFFLE_MS = 2500;
    private static final Pattern NUMBER = Pattern.compile("(?<!\u00a7)\\d+");

    private static final Map<String, String> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<String, String>(256, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > 2048;
                }
            });

    private static Handicap handicap;
    private static long cachedBucket = -1;

    private DyscalculiaText() {}

    public static String apply(String text) {
        if (text == null || !hasDigit(text)) {
            return text;
        }
        if (handicap == null) {
            handicap = Handicap.byId("dyscalculia");
        }
        if (handicap == null || !ClientState.has(handicap)) {
            return text;
        }

        long bucket = System.currentTimeMillis() / SHUFFLE_MS;
        synchronized (CACHE) {
            if (bucket != cachedBucket) {
                CACHE.clear();
                cachedBucket = bucket;
            }
            return CACHE.computeIfAbsent(text, t -> scramble(t, bucket));
        }
    }

    private static boolean hasDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                return true;
            }
        }
        return false;
    }

    private static String scramble(String text, long bucket) {
        Matcher m = NUMBER.matcher(text);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String real = m.group();
            Random rnd = new Random(real.hashCode() * 31L + bucket);
            StringBuilder fake = new StringBuilder(real.length());
            for (int i = 0; i < real.length(); i++) {
                int digit = (i == 0 && real.length() > 1) ? 1 + rnd.nextInt(9) : rnd.nextInt(10);
                fake.append((char) ('0' + digit));
            }
            m.appendReplacement(out, fake.toString());
        }
        m.appendTail(out);
        return out.toString();
    }
}
