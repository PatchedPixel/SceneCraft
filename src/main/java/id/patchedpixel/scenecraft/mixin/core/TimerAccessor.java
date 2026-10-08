package id.patchedpixel.scenecraft.mixin.core;

import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeltaTracker.Timer.class)
public interface TimerAccessor {
    @Accessor("msPerTick")
    float getTickLength();
    @Accessor("msPerTick")
    @Mutable
    void setTickLength(float value);
}
