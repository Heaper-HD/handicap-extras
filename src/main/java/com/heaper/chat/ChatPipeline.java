package com.heaper.chat;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import com.heaper.ExtraHandicapsRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class ChatPipeline {
    private static boolean replaying = false;

    private ChatPipeline() {}

    public static void register() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            HandicapState state = HandicapManager.state();
            if (state == null || state.has(sender.getUUID(), Handicap.MUTE)) {
                return true;
            }

            String original = message.signedContent();
            ChatMessage msg = process(sender, original, state);
            if (!msg.modified()) {
                observe(sender, original);
                return true;
            }
            deliver(msg);
            return false;
        });
    }

    public static void say(ServerPlayer sender, String text) {
        HandicapState state = HandicapManager.state();
        if (state == null || state.has(sender.getUUID(), Handicap.MUTE)) {
            return;
        }
        replaying = true;
        try {
            deliver(process(sender, text, state));
        } finally {
            replaying = false;
        }
    }

    public static boolean isReplaying() {
        return replaying;
    }

    private static ChatMessage process(ServerPlayer sender, String text, HandicapState state) {
        ChatMessage msg = new ChatMessage(sender, text,
        new ArrayList<>(sender.level().getServer().getPlayerList().getPlayers()));
        for (ExtraHandicap h : ExtraHandicapsRegistry.all()) {
            if (state.has(sender.getUUID(), h.handicap())) {
                h.onChat(msg);
            }
        }
        return msg;
    }

    private static void deliver(ChatMessage msg) {
        Component line = Component.translatable("chat.type.text", msg.sender().getDisplayName(), Component.literal(msg.text()));
        for (ServerPlayer recipient : msg.recipients()) {
            recipient.sendSystemMessage(line);
        }
        observe(msg.sender(), msg.text());
    }

    private static void observe(ServerPlayer sender, String text) {
        for (ExtraHandicap h : ExtraHandicapsRegistry.all()) {
            h.observerChat(sender, text);
        }
    }
}
