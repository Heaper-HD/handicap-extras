package com.heaper.mixin;

import com.heaper.extra_handicaps.HeavyHanded;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackDurabilityMixin {
    @Inject(method = "processDurabilityChange", at = @At("RETURN"), cancellable = true)
    private void handicap_extras$heavyHanded(int amount, ServerLevel level, ServerPlayer player,
                                             CallbackInfoReturnable<Integer> cir) {
        if (player != null && HeavyHanded.applies(player)) {
            cir.setReturnValue(cir.getReturnValue() * 2);
        }
    }
}
