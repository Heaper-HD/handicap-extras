package com.heaper.mixin;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void handicap_extras$setTransformation(Transformation transformation);

    @Invoker("setTransformationInterpolationDuration")
    void handicap_extras$setTransformationInterpolationDuration(int ticks);

    @Invoker("setTransformationInterpolationDelay")
    void handicap_extras$setTransformationInterpolationDelay(int ticks);

    @Invoker("setPosRotInterpolationDuration")
    void handicap_extras$setPosRotInterpolationDuration(int ticks);
}
