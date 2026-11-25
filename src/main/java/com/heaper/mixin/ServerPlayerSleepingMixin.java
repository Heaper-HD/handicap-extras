package com.heaper.mixin;

import com.heaper.extra_handicaps.HeavySleeper;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerSleepingMixin {
    @Inject(method = "stopSleepInBed", at = @At("HEAD"), cancellable = true)
    private void handicap_extras$heavySleeper(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci) {
        if (HeavySleeper.blockWake((ServerPlayer) (Object) this)) {
            ci.cancel();
        }
    }
}
