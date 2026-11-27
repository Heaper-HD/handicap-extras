package com.heaper.client.extra_handicaps;

import com.heaper.client.ExtraClientHandicap;
import com.heaper.client.effects.SynesthesiaOverlay;
import com.heaper.client.effects.SynesthesiaOverlay.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import java.util.EnumMap;
import java.util.Map;

public class SynesthesiaClient extends ExtraClientHandicap implements SoundEventListener {
    private static final double SELF_RADIUS = 1.5f;
    private static final long CATEGORY_COOLDOWN = 90;

    private final Map<Kind, Long> lastSpawn = new EnumMap<>(Kind.class);
    private boolean listening = false;
    private boolean active = false;

    public SynesthesiaClient() {
        super("synesthesia");
    }

    @Override
    public void onGive(Minecraft mc) {
        if (!listening) {
            mc.getSoundManager().addListener(this);
            listening = true;
        }
        active = true;
    }

    @Override
    public void onTick(Minecraft mc) {
        active = true;
        SynesthesiaOverlay.CHANNEL.set(id(), 1f);
    }

    @Override
    public void onRemove(Minecraft mc) {
        active = false;
        SynesthesiaOverlay.CHANNEL.clear(id());
        SynesthesiaOverlay.clear();
        lastSpawn.clear();
    }

    private static Kind kindOf(SoundSource source) {
        return switch (source) {
            case HOSTILE -> Kind.HOSTILE;
            case NEUTRAL -> Kind.NEUTRAL;
            case BLOCKS -> Kind.BLOCKS;
            case WEATHER, AMBIENT -> Kind.AMBIENT;
            case PLAYERS -> Kind.PLAYERS;
            default -> null;
        };
    }

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents soundEvent, float range) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (!active || p == null || sound.isRelative()) {
            return;
        }

        Kind kind = kindOf(sound.getSource());
        boolean explosion = sound.getIdentifier().getPath().contains("explode");
        if (kind == null && !explosion) {
            return;
        }

        double dx = sound.getX() - p.getX();
        double dy = sound.getY() - p.getEyeY();
        double dz = sound.getZ() - p.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < SELF_RADIUS) {
            return;
        }

        float loudness = Mth.clamp(sound.getVolume() * (1f - (float) dist / Math.max(range, 1f)), 0f, 1f);
        if (loudness <= 0.02f) {
            return;
        }

        if (explosion) {
            SynesthesiaOverlay.flash(0.4f + 0.5f * loudness);
            if (kind == null) {
                return;
            }
        }

        long now = System.currentTimeMillis();
        Long last = lastSpawn.get(kind);
        if (last != null && now - last < CATEGORY_COOLDOWN) {
            return;
        }
        lastSpawn.put(kind, now);

        float soundYaw = (float) (Math.atan2(-dx, dz) * Mth.RAD_TO_DEG);
        float rel = Mth.wrapDegrees(soundYaw - p.getYRot());
        SynesthesiaOverlay.spawn(kind, rel, loudness, sound.getPitch());
    }
}
