package com.heaper.rope;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.UUID;

public class Rope {
    public static final int POINTS = 28;
    public static final double LENGTH = 10.0;
    public static final double RADIUS = 0.12;
    private static final double SEGMENT = LENGTH / (POINTS - 1);
    private static final double GRAVITY = 0.02;
    private static final double DAMPING = 0.94;
    private static final double GROUND_FRICTION = 0.6;
    private static final int ITERATIONS = 40;
    private static final double EPS = 0.001;
    private static final double TAUT_MARGIN = 0.2;
    private static final double BEND_COS = Math.cos(Math.toRadians(20));

    private static int nextId = 0;

    public final int id = nextId++;
    public final UUID a;
    public final UUID b;

    private final Vec3[] pos = new Vec3[POINTS];
    private final Vec3[] prev = new Vec3[POINTS];
    private final Vec3[] before = new Vec3[POINTS];
    private final boolean[] grounded = new boolean[POINTS];
    private double stretch = 0;
    private double stretchRate = 0;
    private final boolean[] contact = new boolean[POINTS];
    private double path = 0;

    public Rope(UUID a, UUID b, Vec3 from, Vec3 to) {
        this.a = a;
        this.b = b;
        reset(from, to);
    }

    public void reset(Vec3 from, Vec3 to) {
        for (int i = 0; i < POINTS; i++) {
            pos[i] = from.lerp(to, (double) i / (POINTS - 1));
            prev[i] = pos[i];
        }
        stretch = 0;
        stretchRate = 0;
        path = 0;
        stretch = 0;
    }

    public void step(Level level, Vec3 from, Vec3 to, List<? extends Entity> obstacles) {
        java.util.Arrays.fill(contact, false);
        for (int i = 1; i < POINTS - 1; i++) {
            Vec3 start = pos[i];
            Vec3 velocity = pos[i].subtract(prev[i]).scale(DAMPING);
            prev[i] = pos[i];
            pos[i] = pos[i].add(velocity).add(0, -GRAVITY, 0);
            grounded[i] = collide(level, i, start);
        }

        for (int i = 1; i < POINTS - 1; i++) {
            for (Entity e : obstacles) {
                pos[i] = pushOut(pos[i], e.getBoundingBox().inflate(RADIUS));
            }
        }

        for (int it = 0; it < ITERATIONS; it++) {
            pos[0] = from;
            pos[POINTS - 1] = to;
            System.arraycopy(pos, 0, before, 0, POINTS);

            boolean forward = (it % 2) == 0;
            for (int k = 0; k < POINTS - 1; k++) {
                int i = forward ? k : (POINTS - 2 - k);
                Vec3 d = pos[i + 1].subtract(pos[i]);
                double len = d.length();
                if (len <= SEGMENT) {
                    continue;
                }
                Vec3 correction = d.scale((len - SEGMENT) / len * 0.5);
                boolean pinnedLeft = i == 0;
                boolean pinnedRight = i + 1 == POINTS - 1;
                if (pinnedLeft && !pinnedRight) {
                    pos[i + 1] = pos[i + 1].subtract(correction.scale(2));
                } else if (pinnedRight && !pinnedLeft) {
                    pos[i] = pos[i].add(correction.scale(2));
                } else if (!pinnedLeft) {
                    pos[i] = pos[i].add(correction);
                    pos[i + 1] = pos[i + 1].subtract(correction);
                }
            }

            for (int i = 1; i < POINTS - 1; i++) {
                grounded[i] |= collide(level, i, before[i]);
            }
        }

        for (int i = 1; i < POINTS - 1; i++) {
            if (grounded[i]) {
                Vec3 v = pos[i].subtract(prev[i]);
                prev[i] = pos[i].subtract(v.x * (1 - GROUND_FRICTION), v.y, v.z * (1 - GROUND_FRICTION));
            }
        }

        path = 0;
        for (int i = 0; i < POINTS - 1; i++) {
            path += pos[i + 1].distanceTo(pos[i]);
        }
        double newStretch = Math.max(0, path - LENGTH);
        stretchRate = newStretch - stretch;
        stretch = newStretch;
    }

    private boolean collide(Level level, int i, Vec3 from) {
        boolean onTop = false;
        for (int pass = 0; pass < 3; pass++) {
            Vec3 p = pos[i];
            boolean moved = false;
            BlockPos min = BlockPos.containing(p.x - RADIUS, p.y - RADIUS, p.z - RADIUS);
            BlockPos max = BlockPos.containing(p.x + RADIUS, p.y + RADIUS, p.z + RADIUS);

            for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
                VoxelShape shape = level.getBlockState(bp).getCollisionShape(level, bp);
                if (shape.isEmpty()) {
                    continue;
                }
                for (AABB local : shape.toAabbs()) {
                    AABB b = local.move(bp).inflate(RADIUS);
                    if (!b.contains(p)) {
                        continue;
                    }
                    double[] pen = {p.x - b.minX, b.maxX - p.x, p.y - b.minY, b.maxY - p.y, p.z - b.minZ, b.maxZ - p.z};
                    boolean[] entered = {from.x <= b.minX, from.x >= b.maxX, from.y <= b.minY,
                            from.y >= b.maxY, from.z <= b.minZ, from.z >= b.maxZ};

                    int face = -1;
                    for (int f = 0; f < 6; f++) {
                        if (entered[f] && (face < 0 || pen[f] < pen[face])) {
                            face = f;
                        }
                    }
                    if ( face < 0) {
                        face = 0;
                        for (int f = 1; f < 6; f++) {
                            if (pen[f] < pen[face]) {
                                face = f;
                            }
                        }
                    }

                    p = switch (face) {
                        case 0 -> new Vec3(b.minX - EPS, p.y, p.z);
                        case 1 -> new Vec3(b.maxX + EPS, p.y, p.z);
                        case 2 -> new Vec3(p.x, b.minY - EPS, p.z);
                        case 3 -> new Vec3(p.x, b.maxY + EPS, p.z);
                        case 4 -> new Vec3(p.x, p.y, b.minZ - EPS);
                        default -> new Vec3(p.x, p.y, b.maxZ + EPS);
                    };
                    if (face == 3) {
                        onTop = true;
                    }
                    moved = true;
                    contact[i] = true;
                }
            }
            pos[i] = p;
            if (!moved) {
                break;
            }
        }
        return onTop;
    }

    public double stretch() {
        return stretch;
    }

    public double stretchRate() {
        return stretchRate;
    }

    public boolean taut() {
        return path > LENGTH - TAUT_MARGIN;
    }

    private int pivotIndex(boolean endA) {
        if (taut()) {
            if (endA) {
                for (int i = 2; i < POINTS - 1; i++) {
                    if (contact[i] && bends(i)) {
                        return i;
                    }
                }
            } else {
                for (int i = POINTS - 3; i > 0; i--) {
                    if (contact[i] && bends(i)) {
                        return i;
                    }
                }
            }
        }
        return endA ? POINTS - 1 : 0;
    }

    private boolean bends(int i) {
        Vec3 in = pos[i].subtract(pos[i - 1]);
        Vec3 out = pos[i + 1].subtract(pos[i]);
        double l = in.length() * out.length();
        return l > 1e-8 && in.dot(out) / l < BEND_COS;
    }

    public Vec3 pivot(boolean endA) {
        return pos[pivotIndex(endA)];
    }

    public double allowed(boolean endA) {
        int k = pivotIndex(endA);
        double rest = 0;
        if (endA) {
            for (int i = k; i < POINTS - 1; i++) {
                rest += pos[i + 1].distanceTo(pos[i]);
            }
        } else {
            for (int i = 0; i < k; i++) {
                rest += pos[i + 1].distanceTo(pos[i]);
            }
        }
        return Math.max(0.5, LENGTH - rest);
    }

    public Vec3 pullDirection(boolean endA) {
        Vec3 d = endA ? pos[1].subtract(pos[0]) : pos[POINTS - 2].subtract(pos[POINTS - 1]);
        return d.lengthSqr() < 1e-8 ? Vec3.ZERO : d.normalize();
    }

    public Vec3 point(int i) {
        return pos[i];
    }

    public List<Vec3> points() {
        return List.of(pos.clone());
    }

    public AABB bounds() {
        AABB box = new AABB(pos[0], pos[0]);
        for (Vec3 p : pos) {
            box = box.minmax(new AABB(p, p));
        }
        return box;
    }

    private static Vec3 pushOut(Vec3 p, AABB box) {
        if (!box.contains(p)) {
            return p;
        }
        Vec3 c = box.getCenter();
        double penX = box.getXsize() / 2 - Math.abs(p.x - c.x);
        double penZ = box.getZsize() / 2 - Math.abs(p.z - c.z);
        if (penX < penZ) {
            return new Vec3(p.x >= c.x ? box.maxX : box.minX, p.y, p.z);
        }
        return new Vec3(p.x, p.y, p.z >= c.z ? box.maxZ : box.minZ);
    }
}
