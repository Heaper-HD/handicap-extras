package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.HandicapCheck;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;

public class HoardingDisorder extends ExtraHandicap {
    private static HoardingDisorder instance;

    private static final ThreadLocal<Player> CLICKING = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> DENIED = ThreadLocal.withInitial(() -> false);

    public HoardingDisorder() {
        super("hoarding_disorder");
        instance = this;
    }

    private static boolean isStorage(AbstractContainerMenu menu) {
        return menu instanceof ChestMenu || menu instanceof ShulkerBoxMenu
                || menu instanceof HopperMenu || menu instanceof DispenserMenu;
    }

    public static void begin(AbstractContainerMenu menu, Player player) {
        boolean hoarding = instance != null && isStorage(menu) && HandicapCheck.has(player, instance.handicap());
        CLICKING.set(hoarding ? player : null);
        DENIED.set(false);
    }

    public static void end(Player player) {
        if (DENIED.get() && player instanceof ServerPlayer sp) {
            sp.sendOverlayMessage(Component.literal("You can't bear to part with it!"));
        }
        CLICKING.remove();
        DENIED.set(false);
    }

    public static boolean mayPlace(Slot slot) {
        if (CLICKING.get() == null || slot.container instanceof net.minecraft.world.entity.player.Inventory) {
            return true;
        }
        DENIED.set(true);
        return false;
    }
}
