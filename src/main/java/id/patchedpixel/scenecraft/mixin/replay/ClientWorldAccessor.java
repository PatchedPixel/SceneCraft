package id.patchedpixel.scenecraft.mixin.replay;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public interface ClientWorldAccessor {
    @Accessor("tickingEntities")
    net.minecraft.world.level.entity.EntityTickList getEntityList();
}
