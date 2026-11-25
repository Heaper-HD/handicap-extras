package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import net.minecraft.util.RandomSource;

public class Stutter extends ExtraHandicap {
    private static final float CHANCE = 0.3f;

    public Stutter() {
        super("stutter");
    }

    @Override
    public void onChat(ChatMessage msg) {
        RandomSource rnd = msg.sender().getRandom();
        String[] words = msg.text().split(" ", -1);
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.length() > 1 && Character.isLetter(w.charAt(0)) && rnd.nextFloat() < CHANCE) {
                String first = w.substring(0, 1);
                words[i] = (first + "-").repeat(1 + rnd.nextInt(2)) + w;
            }
        }
        msg.setText(String.join(" ", words));
    }
}
