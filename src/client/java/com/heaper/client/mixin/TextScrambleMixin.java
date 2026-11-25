package com.heaper.client.mixin;

import com.heaper.client.text.*;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StringDecomposer.class)
public abstract class TextScrambleMixin {
    @ModifyVariable(method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
            at = @At("HEAD"), argsOnly = true)
    private static String handicap_extras$scrambleFormatted(String text) {
        return handicap_extras$scramble(text);
    }

    private static String handicap_extras$scramble(String text) {
        return MirrorText.apply(LongWordText.apply(DirectionText.apply(DyscalculiaText.apply(text))));
    }
}