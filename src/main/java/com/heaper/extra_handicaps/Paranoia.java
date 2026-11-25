package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class Paranoia extends ExtraHandicap {
    private static final int ONE_IN = 90;

    private static final List<String> LINES = List.of(
            "let's ditch {me} at the next cave",
            "i took {me}'s diamonds lol, don't tell",
            "{me} is so slow it's embarrassing",
            "shh, {me} can't read chat",
            "anyone else think {me} is useless?",
            "i'm hiding {me}'s bed",
            "lmao {me} has no idea",
            "we should vote {me} out",
            "don't trust {me}",
            "ok when {me} logs off we go to the end"
    );

    public Paranoia() {
        super("paranoia");
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (p.getRandom().nextInt(ONE_IN) != 0) {
            return;
        }

        List<ServerPlayer> others = new ArrayList<>(p.level().getServer().getPlayerList().getPlayers());
        others.remove(p);
        String name = others.isEmpty()
                ? "Someone"
                : others.get(p.getRandom().nextInt(others.size())).getName().getString();

        String line = LINES.get(p.getRandom().nextInt(LINES.size())).replace("{me}", p.getName().getString());

        p.sendSystemMessage(Component.translatable("chat.type.text", Component.literal(name), Component.literal(line)));
    }
}
