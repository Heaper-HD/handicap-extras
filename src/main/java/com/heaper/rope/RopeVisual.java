package com.heaper.rope;

import com.heaper.mixin.BlockDisplayAccessor;
import com.heaper.mixin.DisplayAccessor;
import com.mojang.math.Transformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class RopeVisual {
    public static final String TAG = "handicap_extras_rope";

    private static final float THICKNESS = 0.2f;
    private static final BlockState MATERIAL = Blocks.NETHER_WART_BLOCK.defaultBlockState();

    private static final Set<UUID> LIVE = new HashSet<>();

    private final ServerLevel level;
    private final List<Display.BlockDisplay> segments = new ArrayList<>();

    public RopeVisual(ServerLevel level, Rope rope) {
        this.level = level;
        for (int i = 0; i < Rope.POINTS - 1; i++) {
            Display.BlockDisplay d = new Display.BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);
            ((BlockDisplayAccessor) d).handicap_extras$setBlockState(MATERIAL);
            DisplayAccessor a = (DisplayAccessor) d;
            a.handicap_extras$setPosRotInterpolationDuration(1);
            a.handicap_extras$setTransformationInterpolationDuration(1);
            d.addTag(TAG);
            place(d, rope.point(i), rope.point(i + 1));

            LIVE.add(d.getUUID());
            level.addFreshEntity(d);
            segments.add(d);
        }
    }

    public static boolean isLive(Entity e) {
        return LIVE.contains(e.getUUID());
    }

    public boolean isIn(ServerLevel other) {
        return level == other;
    }

    public void update(Rope rope) {
        for (int i = 0; i < segments.size(); i++) {
            place(segments.get(i), rope.point(i), rope.point(i + 1));
        }
    }

    public void remove() {
        for (Display.BlockDisplay d : segments) {
            LIVE.remove(d.getUUID());
            d.discard();
        }
        segments.clear();
    }

    private static void place(Display.BlockDisplay d, Vec3 from, Vec3 to) {
        d.setPos(from.x, from.y, from.z);

        Vector3f dir = new Vector3f((float) (to.x - from.x), (float) (to.y - from.y), (float) (to.z - from.z));
        float len = dir.length();
        Quaternionf rot = len < 1e-4f
                ? new Quaternionf()
                : new Quaternionf().rotationTo(0, 0, 1, dir.x / len, dir.y / len, dir.z / len);

        float t = THICKNESS;
        Vector3f translation = rot.transform(new Vector3f(-t / 2, -t / 2, -t / 2));
        Transformation transform = new Transformation(translation, rot, new Vector3f(t, t, len + t), new Quaternionf());

        DisplayAccessor a = (DisplayAccessor) d;
        a.handicap_extras$setTransformation(transform);
        a.handicap_extras$setTransformationInterpolationDelay(0);
    }
}
