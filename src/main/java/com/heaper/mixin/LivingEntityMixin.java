package com.heaper.mixin;

import com.heaper.ExtraHandicapManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private ItemStack handicap_extras$consuming = null;

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void handicap_extras$beforeConsume(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ServerPlayer && self.isUsingItem() && !self.getUseItem().isEmpty()) {
            handicap_extras$consuming = self.getUseItem().copy();
        }
    }

    @Inject(method = "completeUsingItem", at = @At("TAIL"))
    private void handicap_extras$consuming(CallbackInfo ci) {
        ItemStack item = handicap_extras$consuming;
        handicap_extras$consuming = null;
        if (item != null && (Object) this instanceof ServerPlayer p) {
            ExtraHandicapManager.onConsume(p, item);
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN"))
    private void handicap_extras$afterAddEffect(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && (Object) this instanceof ServerPlayer p) {
            ExtraHandicapManager.onEffectAdded(p, effect);
        }
    }
}
