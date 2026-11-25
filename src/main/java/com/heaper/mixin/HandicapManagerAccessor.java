package com.heaper.mixin;

import com.handicap.HandicapManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(HandicapManager.class)
public interface HandicapManagerAccessor {
    @Accessor("RUNTIME")
    static Map<UUID, ?> handicap_extras$runtime() {
        throw new AssertionError();
    }
}
