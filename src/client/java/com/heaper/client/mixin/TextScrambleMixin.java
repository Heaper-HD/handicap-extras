package com.heaper.client.mixin;

import com.heaper.text.DirectionText;
import com.heaper.text.DyscalculiaText;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StringDecomposer.class)
public abstract class TextScrambleMixin {
    @ModifyVariable(method = {"iterate", "iterateFormatted"}, at = @At("HEAD"), argsOnly = true)
    private static String handicap_extra$textScramble(String text) {
        return DirectionText.apply(DyscalculiaText.apply(text));
    }
}
