package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ForeignAccent extends ExtraHandicap {
    private enum Style { PIRATE, SHAKESPEAREAN, ROBOT, CAVEMAN }

    private final Map<UUID, Style> styles = new HashMap<>();

    public ForeignAccent () {
        super("foreign_accent");
    }

    @Override
    public void onGive(ServerPlayer p) {
        styles.computeIfAbsent(p.getUUID(), id -> Style.values()[p.getRandom().nextInt(Style.values().length)]);
    }

    @Override
    public void onChat(ChatMessage msg) {
        Style style = styles.get(msg.sender().getUUID());
        if (style == null) {
            return;
        }
        String t = msg.text();
        msg.setText(switch (style) {
            case PIRATE -> words(t, Map.of("you", "ye", "your", "yer", "my", "me", "is", "be", "are", "be",
                    "hello", "ahoy", "hi", "ahoy", "yes", "aye", "friend", "matey")) + " Arr!";
            case SHAKESPEAREAN -> "Verily, " + words(t, Map.of("you", "thou", "your", "thy", "are", "art",
                    "yes", "aye", "no", "nay", "hello", "hail", "hi", "hail"));
            case ROBOT -> "BEEP. " + t.toUpperCase(Locale.ROOT) + ". BOOP.";
            case CAVEMAN -> words(t, Map.of("i", "me", "the", "", "a", "", "an", "", "is", "", "are", "")) + ". ugh.";
        });
    }

    private static String words(String text, Map<String, String> swaps) {
        StringBuilder out = new StringBuilder();
        for (String token : text.split(" ")) {
            int end = token.length();
            while (end > 0 && !Character.isLetterOrDigit(token.charAt(end - 1))) {
                end--;
            }
            String core = token.substring(0, end);
            String tail = token.substring(end);

            String swap = swaps.get(core.toLowerCase(Locale.ROOT));
            String word = core;
            if (swap != null) {
                word = (!swap.isEmpty() && !core.isEmpty() && Character.isUpperCase(core.charAt(0))
                        ? Character.toUpperCase(swap.charAt(0)) + swap.substring(1)
                        : swap);
            }

            if (word.isEmpty()) {
                out.append(tail);
                continue;
            }
            if (!out.isEmpty()) {
                out.append(' ');
            }
            out.append(word).append(tail);
        }
        return out.toString();
    }

    @Override
    public void onRemove(ServerPlayer p) {
        styles.remove(p.getUUID());
    }
}
