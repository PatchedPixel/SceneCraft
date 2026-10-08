package id.patchedpixel.scenecraft.mixin.record;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;

import net.minecraft.network.syncher.EntityDataAccessor;

@Mixin(LivingEntity.class)
public interface EntityLivingBaseAccessor {
    @Accessor("DATA_LIVING_ENTITY_FLAGS")
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    static EntityDataAccessor<Byte> getLivingFlags() { return null; }
}
