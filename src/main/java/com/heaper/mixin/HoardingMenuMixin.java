package com.heaper.mixin;

import com.heaper.extra_handicaps.HoardingDisorder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class HoardingMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"))
    private void handicap_extras$begin(int slotId, int button, ContainerInput input, Player player, CallbackInfo ci) {
        HoardingDisorder.begin((AbstractContainerMenu) (Object) this, player);
    }

    @Inject(method = "clicked", at = @At("RETURN"))
    private void handicap_extras$end(int slotId, int button, ContainerInput input, Player player, CallbackInfo ci) {
        HoardingDisorder.end(player);
    }
}
