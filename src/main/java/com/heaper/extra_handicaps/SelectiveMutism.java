package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import com.heaper.util.Nearby;

import java.util.List;

public class SelectiveMutism extends ExtraHandicap {
    private static final double RANGE = 16.0;

    public SelectiveMutism() {
        super("selective_mutism");
    }

    @Override
    public void onChat(ChatMessage msg) {
        if (!Nearby.players(msg.sender(), RANGE).isEmpty()) {
            msg.setRecipients(List.of());
            overlay(msg.sender(), "You freeze up... too many people around.");
        }
    }
}
