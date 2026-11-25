package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class BrokenTelephone extends ExtraHandicap {
    public BrokenTelephone() {
        super("broken_telephone");
    }

    @Override
    public void onChat(ChatMessage msg) {
        List<ServerPlayer> others = new ArrayList<>(msg.recipients());
        others.remove(msg.sender());
        if (others.isEmpty()) {
            return;
        }
        ServerPlayer lucky = others.get(msg.sender().getRandom().nextInt(others.size()));
        msg.setRecipients(List.of(msg.sender(), lucky));
    }
}
