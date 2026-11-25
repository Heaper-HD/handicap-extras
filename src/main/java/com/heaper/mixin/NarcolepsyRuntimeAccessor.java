package com.heaper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.handicap.HandicapManager$Runtime")
public interface NarcolepsyRuntimeAccessor {
    @Accessor("narcoSeconds")
    int handicap_extras$getNarcoSeconds();

    @Accessor("narcoSeconds")
    void handicap_extras$setNarcoSeconds(int seconds);
}
