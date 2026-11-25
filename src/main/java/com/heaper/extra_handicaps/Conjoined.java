package com.heaper.extra_handicaps;

import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import com.heaper.network.ImpulsePayload;
import com.heaper.network.StrainPayload;
import com.heaper.network.TetherPayload;
import com.heaper.rope.Rope;
import com.heaper.rope.RopeVisual;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.List;

public class Conjoined extends ExtraHandicap {
    private static final double HARD_LIMIT = 24.0;
    private static final double TENSION = 0.06;
    private static final double STIFF_AFTER = 2.0;
    private static final double STIFFNESS = 0.25;
    private static final double DAMPING = 0.6;
    private static final double MAX_TENSION = 0.5;
    private static final double TAUT = 0.3;
    private static final double SHOVE = 0.15;
    private static final double RESET_DISTANCE = 4.0;
    private static final double SLACK = 0.25;
    private static final double TRANSFER = 0.5;
    private static final double MAX_STRAIN = 1.0;

    private static final float DAMAGE_SHARE = 1f;

    private boolean sharing = false;

    private final Map<UUID, UUID> partners = new HashMap<>();
    private final Map<UUID, Rope> ropes = new HashMap<>();
    private final Map<Rope, RopeVisual> visuals = new HashMap<>();
    private final Map<UUID, Vec3> lastPos = new HashMap<>();
    private final Map<UUID, ResourceKey<Level>> lastLevel = new HashMap<>();

    public Conjoined() {
        super("conjoined");
    }

    private static MinecraftServer server(ServerPlayer p) {
        return p.level().getServer();
    }

    private static Vec3 anchor(ServerPlayer p) {
        return p.position().add(0, p.getBbHeight() * 0.5, 0);
    }

    @Override
    public void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(this::tickRopes);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            visuals.values().forEach(RopeVisual::remove);
            visuals.clear();
        });

        ServerEntityEvents.ENTITY_LOAD.register(((entity, level) -> {
            if (entity.tags().toList().contains(RopeVisual.TAG) && !RopeVisual.isLive(entity)) {
                entity.discard();
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(StrainPayload.TYPE, (payload, context) -> {
            ServerPlayer sender = context.player();
            UUID partnerId = partners.get(sender.getUUID());
            if (partnerId == null) {
                return;
            }
            ServerPlayer partner = sender.level().getServer().getPlayerList().getPlayer(partnerId);
            if (partner == null || partner.level() != sender.level()) {
                return;
            }
            Vec3 strain = new Vec3(payload.x(), payload.y(), payload.z());
            if (strain.length() > MAX_STRAIN) {
                strain = strain.normalize().scale(MAX_STRAIN);
            }
            push(partner, strain.scale(TRANSFER));
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (sharing || blocked || damageTaken <= 0f || !(entity instanceof ServerPlayer p)) {
                return;
            }
            ServerPlayer partner = partnerOf(p);
            if (partner == null) {
                return;
            }
            sharing = true;
            try {
                partner.hurtServer(partner.level(), partner.damageSources().generic(), damageTaken * DAMAGE_SHARE);
            } finally {
                sharing = false;
            }
        });
    }

    @Override
    public void onEffectAdded(ServerPlayer p, MobEffectInstance effect) {
        if (sharing || !effect.showIcon()) {
            return;
        }
        ServerPlayer partner = partnerOf(p);
        if (partner == null) {
            return;
        }
        sharing = true;
        try {
            partner.addEffect(new MobEffectInstance(effect), p);
        } finally {
            sharing = false;
        }
    }

    @Override
    public void onGive(ServerPlayer p) {
        if (!partners.containsKey(p.getUUID())) {
            tryLink(p);
        }
    }

    private void tryLink(ServerPlayer p) {
        List<ServerPlayer> withHandicap = new ArrayList<>();
        List<ServerPlayer> anyone = new ArrayList<>();
        for (ServerPlayer other : server(p).getPlayerList().getPlayers()) {
            if (other == p || partners.containsKey(other.getUUID())) {
                continue;
            }
            anyone.add(other);
            if (has(other)) {
                withHandicap.add(other);
            }
        }
        List<ServerPlayer> pool = !withHandicap.isEmpty() ? withHandicap : anyone;
        if (pool.isEmpty()) {
            return;
        }

        ServerPlayer partner = pool.get(p.getRandom().nextInt(pool.size()));
        partners.put(p.getUUID(), partner.getUUID());
        partners.put(partner.getUUID(), p.getUUID());
        Rope rope = new Rope(p.getUUID(), partner.getUUID(), anchor(p), anchor(partner));
        ropes.put(p.getUUID(), rope);
        ropes.put(partner.getUUID(), rope);
        if (!has(partner)) {
            HandicapManager.give(partner, handicap(), false);
        }

        server(p).getPlayerList().broadcastSystemMessage(
                Component.literal(p.getName().getString() + " and " + partner.getName().getString() + " are now conjoined!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), false);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        lastPos.remove(p.getUUID());
        lastLevel.remove(p.getUUID());
        ropes.remove(p.getUUID());

        UUID partnerId = partners.remove(p.getUUID());
        if (partnerId == null) {
            return;
        }
        partners.remove(partnerId);
        Rope rope = ropes.remove(partnerId);
        if (rope != null) {
            RopeVisual visual = visuals.remove(rope);
            if (visual != null) {
                visual.remove();
            }
        }

        ServerPlayer partner = server(p).getPlayerList().getPlayer(partnerId);
        if (partner != null) {
            HandicapManager.remove(partner, handicap(), false);
        } else {
            HandicapState state = HandicapManager.state();
            if (state != null) {
                state.remove(partnerId, handicap());
            }
        }
    }

    @Override
    public void forget(UUID player) {
        lastPos.remove(player);
        lastLevel.remove(player);
    }

    @Override
    public void onTick(ServerPlayer p) {
        UUID partnerId = partners.get(p.getUUID());
        if (partnerId == null) {
            tryLink(p);
            return;
        }

        ServerPlayer partner = server(p).getPlayerList().getPlayer(partnerId);
        if (partner != null && !p.isSpectator() && !partner.isSpectator()) {
            ResourceKey<Level> previous = lastLevel.get(p.getUUID());
            boolean changedDimension = previous != null && previous != p.level().dimension();
            
            if (partner.level() != p.level()) {
                if (changedDimension) {
                    pullTo(partner, p);
                }
            } else if (p.distanceToSqr(partner) > HARD_LIMIT * HARD_LIMIT && moved(p) >= moved(partner)) {
                pullTo(p, partner);
            }
        }

        lastPos.put(p.getUUID(), p.position());
        lastLevel.put(p.getUUID(), p.level().dimension());
    }

    private double moved(ServerPlayer p) {
        Vec3 last = lastPos.get(p.getUUID());
        return last == null ? 0 : last.distanceTo(p.position());
    }

    private void pullTo(ServerPlayer who, ServerPlayer to) {
        who.teleportTo(to.level(), to.getX(), to.getY(), to.getZ(), Set.of(), who.getYRot(), who.getXRot(), true);
        lastPos.put(who.getUUID(), who.position());
        lastLevel.put(who.getUUID(), who.level().dimension());
        overlay(who, "You're dragged back to " + to.getName().getString() + "!");
    }

    private void tickRopes(MinecraftServer server) {
        Set<Rope> done = new HashSet<>();
        for (Rope rope : ropes.values()) {
            if (!done.add(rope)) {
                continue;
            }
            ServerPlayer pa = server.getPlayerList().getPlayer(rope.a);
            ServerPlayer pb = server.getPlayerList().getPlayer(rope.b);
            if (pa == null || pb == null || pa.level() != pb.level() || pa.isSpectator() || pb.isSpectator()) {
                RopeVisual hidden = visuals.remove(rope);
                if (hidden != null) {
                    hidden.remove();
                }
                continue;
            }

            ServerLevel level = pa.level();
            Vec3 from = anchor(pa);
            Vec3 to = anchor(pb);
            if (rope.point(0).distanceTo(from) > RESET_DISTANCE || rope.point(Rope.POINTS - 1).distanceTo(to) > RESET_DISTANCE) {
                rope.reset(from ,to);
            }

            List<LivingEntity> obstacles = level.getEntitiesOfClass(LivingEntity.class, rope.bounds().inflate(1.0),
                    e -> e != pa && e != pb && !e.isSpectator() && e.isAlive());

            rope.step(level, from, to, obstacles);

            sendTether(pa, rope, true);
            sendTether(pb, rope, false);

            if (rope.stretch() > TAUT) {
                for (LivingEntity e : obstacles) {
                    for (int i = 1; i < Rope.POINTS - 1; i++) {
                        Vec3 pt = rope.point(i);
                        if (e.getBoundingBox().inflate(Rope.RADIUS).contains(pt)) {
                            Vec3 away = new Vec3(e.getX() - pt.x, 0, e.getZ() - pt.z);
                            if (away.lengthSqr() > 1e-6) {
                                push(e, away.normalize().scale(SHOVE).add(0, 0.05, 0));
                            }
                            break;
                        }
                    }
                }
            }

            RopeVisual visual = visuals.get(rope);
            if (visual == null || !visual.isIn(level)) {
                if (visual != null) {
                    visual.remove();
                }
                visuals.put(rope, new RopeVisual(level, rope));
            } else {
                visual.update(rope);
            }
        }
    }

    private static void push(Entity e, Vec3 impulse) {
        if (e instanceof ServerPlayer p) {
            if (ServerPlayNetworking.canSend(p, ImpulsePayload.TYPE)) {
                ServerPlayNetworking.send(p, new ImpulsePayload((float) impulse.x, (float) impulse.y, (float) impulse.z));
            }
        } else {
            e.setDeltaMovement(e.getDeltaMovement().add(impulse));
        }
    }

    private static void sendTether(ServerPlayer p, Rope rope, boolean endA) {
        if (ServerPlayNetworking.canSend(p, TetherPayload.TYPE)) {
            Vec3 pivot = rope.pivot(endA);
            ServerPlayNetworking.send(p, new TetherPayload(pivot.x, pivot.y, pivot.z, (float) rope.allowed(endA)));
        }
    }

    private ServerPlayer partnerOf(ServerPlayer p) {
        UUID id = partners.get(p.getUUID());
        if (id == null) {
            return null;
        }
        ServerPlayer partner = server(p).getPlayerList().getPlayer(id);
        return partner != null && partner.level() == p.level() && partner.isAlive() ? partner : null;
    }
}
