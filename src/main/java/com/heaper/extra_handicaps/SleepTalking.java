package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SleepTalking extends ExtraHandicap {
    private static final int EVERY_SECONDS = 4;
    private static final List<String> MUMBLES = List.of(
            "creeper... aw man...", "five more minutes...", "mom, I don't want to mine today...",
            "the diamonds are mine...", "who took my bed..."
    );

    private final Map<UUID, Integer> asleepSeconds = new HashMap<>();
    private final Map<UUID, String> lastSaid = new HashMap<>();

    public SleepTalking() {
        super("sleep_talking");
    }

    @Override
    public void observerChat(ServerPlayer sender, String text) {
        lastSaid.put(sender.getUUID(), text);
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (!p.isSleeping()) {
            asleepSeconds.remove(p.getUUID());
            return;
        }
        int seconds = asleepSeconds.merge(p.getUUID(), 1, Integer::sum);
        if (seconds % EVERY_SECONDS != 0) {
            return;
        }

        String line = switch (p.getRandom().nextInt(3)) {
            case 0 -> "I'm at " + p.getBlockX() + ", " + p.getBlockY() + ", " + p.getBlockZ() + "...";
            case 1 -> lastSaid.getOrDefault(p.getUUID(), MUMBLES.get(0));
            default -> MUMBLES.get(p.getRandom().nextInt(MUMBLES.size()));
        };
        Component msg = Component.literal(p.getName().getString() + " mumbles in their sleep: \"" + line + "\"")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        p.level().getServer().getPlayerList().broadcastSystemMessage(msg, false);
    }

    @Override
    public void forget(UUID player) {
        asleepSeconds.remove(player);
    }
}
