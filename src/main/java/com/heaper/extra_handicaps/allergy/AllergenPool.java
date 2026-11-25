package com.heaper.extra_handicaps.allergy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AllergenPool {
    private static final float MOB_CHANCE = 0.4f;
    private static final float BLOCK_CHANCE = 0.4f;

    private static final List<Allergen> MOBS = List.of(
            new MobAllergen("mob:cat", "cats", Set.of(EntityTypes.CAT, EntityTypes.OCELOT)),
            new MobAllergen("mob:dog", "dogs", Set.of(EntityTypes.WOLF)),
            new MobAllergen("mob:horse", "horses", Set.of(EntityTypes.HORSE, EntityTypes.DONKEY, EntityTypes.MULE)),
            new MobAllergen("mob:chicken", "chickens", Set.of(EntityTypes.CHICKEN)),
            new MobAllergen("mob:sheep", "sheep", Set.of(EntityTypes.SHEEP)),
            new MobAllergen("mob:bee", "bees", Set.of(EntityTypes.BEE)),
            new MobAllergen("mob:rabbit", "rabbits", Set.of(EntityTypes.RABBIT)),
            new MobAllergen("mob:parrot", "parrots", Set.of(EntityTypes.PARROT))
    );

    private static final List<Allergen> BLOCKS = List.of(
            new BlockAllergen("block:hay", "hay bales", s -> s.is(Blocks.HAY_BLOCK)),
            new BlockAllergen("block:wool", "wool", s -> s.is(BlockTags.WOOL)),
            new BlockAllergen("block:leaves", "leaves", s -> s.is(BlockTags.LEAVES)),
            new BlockAllergen("block:cobweb", "cobwebs", s -> s.is(Blocks.COBWEB))
    );

    private AllergenPool() {}

    public static Allergen pick(ServerPlayer p) {
        RandomSource rnd = p.getRandom();
        float roll = rnd.nextFloat();

        if (roll >= MOB_CHANCE + BLOCK_CHANCE) {
            List<ServerPlayer> others = new ArrayList<>();
            for (ServerPlayer other : p.level().getServer().getPlayerList().getPlayers()) {
                if (other != p) {
                    others.add(other);
                }
            }
            if (!others.isEmpty()) {
                ServerPlayer target = others.get(rnd.nextInt(others.size()));
                return new PlayerAllergen(target.getUUID(), target.getName().getString());
            }
            roll = rnd.nextFloat() * (MOB_CHANCE + BLOCK_CHANCE);
        }

        List<Allergen> pool = roll < MOB_CHANCE ? MOBS : BLOCKS;
        return pool.get(rnd.nextInt(pool.size()));
    }
}
