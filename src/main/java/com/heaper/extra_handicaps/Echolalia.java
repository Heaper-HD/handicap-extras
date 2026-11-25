package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatPipeline;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Echolalia extends ExtraHandicap {
    private static final float CHANCE = 0.25f;

    private final Map<UUID, String> pending = new HashMap<>();

    public Echolalia() {
        super("echolalia");
    }

    @Override
    public void observerChat(ServerPlayer sender, String text) {
        if (ChatPipeline.isReplaying()) {
            return;
        }
        for (ServerPlayer p : sender.level().getServer().getPlayerList().getPlayers()) {
            if (p != sender && has(p) && p.getRandom().nextFloat() < CHANCE) {
                pending.put(p.getUUID(), text);
            }
        }
    }

    @Override
    public void onTick(ServerPlayer p) {
        String echo = pending.remove(p.getUUID());
        if (echo != null) {
            ChatPipeline.say(p, echo);
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        pending.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        pending.remove(player);
    }
}
