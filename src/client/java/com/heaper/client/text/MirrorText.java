package com.heaper.client.text;

import com.handicap.Handicap;
import com.handicap.client.ClientState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MirrorText {
    private static final Pattern WORD = Pattern.compile("(?<!\u00a7)\\p{L}{3,}");
    private static Handicap handicap;

    private MirrorText() {}

    public static boolean active() {
        if (handicap == null) {
            handicap = Handicap.byId("mirror_writing");
        }
        return handicap != null && TextEffects.allowed() && ClientState.has(handicap);
    }

    public static String apply(String text) {
        if (text == null || text.length() < 3) {
            return text;
        }

        if (!active()) {
            return text;
        }

        Matcher m = WORD.matcher(text);
        StringBuilder out = new StringBuilder(text.length());
        while (m.find()) {
            m.appendReplacement(out, new StringBuilder(m.group()).reverse().toString());
        }
        m.appendTail(out);
        return out.toString();
    }
}
