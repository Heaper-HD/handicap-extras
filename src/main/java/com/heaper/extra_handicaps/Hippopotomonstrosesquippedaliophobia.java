package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.chat.ChatMessage;
import com.heaper.util.Symptoms;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.regex.Pattern;

public class Hippopotomonstrosesquippedaliophobia extends ExtraHandicap {
    public static final Pattern LONG_WORD = Pattern.compile("\\p{L}{12,}");

    public Hippopotomonstrosesquippedaliophobia() {
        super("hippopotomonstrosesquippedaliophobia");
    }

    @Override
    public void onChat(ChatMessage msg) {
        if (LONG_WORD.matcher(msg.text()).find()) {
            msg.setRecipients(List.of());
            overlay(msg.sender(), "You can't bring yourself to say that word!");
        }
    }

    @Override
    public void observerChat(ServerPlayer sender, String text) {
        if (!LONG_WORD.matcher(text).find()) {
            return;
        }
        for (ServerPlayer p : sender.level().getServer().getPlayerList().getPlayers()) {
            if (p != sender && has(p)) {
                panic(p);
            }
        }
    }

    private void panic(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1, false, false , false));
        p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 1, false, false , false));
        Symptoms.jolt(p, -10f, (p.getRandom().nextFloat() - 0.5f) * 20f);
        overlay(p, "That word! It's too long!");
    }
}
