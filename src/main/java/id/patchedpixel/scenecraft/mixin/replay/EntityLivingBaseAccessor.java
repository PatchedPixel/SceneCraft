package id.patchedpixel.scenecraft.mixin.replay;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface EntityLivingBaseAccessor {

    @Accessor("useItemRemaining")
    int getActiveItemStackUseCount();
    @Accessor("useItemRemaining")
    void setActiveItemStackUseCount(int value);
}
