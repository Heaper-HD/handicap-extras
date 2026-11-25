package com.heaper.client.mixin;

import com.heaper.client.text.DirectionText;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassAngleState.class)
public class CompassAngleMixin {
    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void handicap_extras$lie(CallbackInfoReturnable<Float> cir) {
        if (DirectionText.active()) {
            float angle = cir.getReturnValueF() + DirectionText.compassOffset();
            cir.setReturnValue(angle - (float) Math.floor(angle));
        }
    }
}
