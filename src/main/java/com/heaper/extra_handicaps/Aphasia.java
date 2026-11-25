package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Aphasia extends ExtraHandicap {
    private static final float CHANCE = 0.3f;
    private static final int POOL_SIZE = 60;

    private final Deque<String> recentWords = new ArrayDeque<>();

    public Aphasia() {
        super("aphasia");
    }

    @Override
    public void observerChat(ServerPlayer sender, String text) {
        for (String w : text.split(" ")) {
            if (!w.isBlank()) {
                recentWords.addLast(w);
                if (recentWords.size() > POOL_SIZE) {
                    recentWords.removeFirst();
                }
            }
        }
    }

    @Override
    public void onChat(ChatMessage msg) {
        if (recentWords.isEmpty()) {
            return;
        }
        List<String> pool = new ArrayList<>(recentWords);
        RandomSource rnd = msg.sender().getRandom();
        String[] words = msg.text().split(" ", -1);
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isBlank() && rnd.nextFloat() < CHANCE) {
                words[i] = pool.get(rnd.nextInt(pool.size()));
            }
        }
        msg.setText(String.join(" ", words));
    }
}
