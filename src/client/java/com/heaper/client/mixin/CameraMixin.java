package com.heaper.client.mixin;

import com.heaper.client.effects.CameraShake;
import com.heaper.client.extra_handicaps.SeparationAnxietyClient;
import net.minecraft.client.Camera;
import net.minecraft.util.Util;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    @Final
    private Quaternionf rotation;

    private static final float MAX_DEGRESS = 1.2f;

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void handicap_extras$anxietyShake(CallbackInfo ci) {
        float intensity = CameraShake.current();
        if (intensity <= 0f) {
            return;
        }

        double t = Util.getMillis() / 1000.0;
        float roll = (float) (Math.sin(t * 13.0) * 0.6 + Math.sin(t * 29.0) * 0.4);
        float pitch = (float) (Math.sin(t * 17.0) * 0.5 + Math.sin(t * 37.0) * 0.5);

        float amount = MAX_DEGRESS * intensity;
        rotation.rotateZ((float) Math.toRadians(roll * amount));
        rotation.rotateX((float) Math.toRadians(pitch * amount * 0.5f));
    }
}
