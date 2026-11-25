package com.heaper.mixin;

import com.heaper.ExtraHandicapManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void handicap_extras$onTouch(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (player instanceof ServerPlayer sp && !self.hasPickUpDelay() && !self.getItem().isEmpty()) {
            ExtraHandicapManager.onPickup(sp, self);
        }
    }
}
