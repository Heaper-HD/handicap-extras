package com.heaper.mixin;

import com.heaper.extra_handicaps.HoardingDisorder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class HoardingSlotMixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void handicap_extras$hoard(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!HoardingDisorder.mayPlace((Slot) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
