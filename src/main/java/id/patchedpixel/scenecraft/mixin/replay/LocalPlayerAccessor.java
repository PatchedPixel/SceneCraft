package id.patchedpixel.scenecraft.mixin.replay;

import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    @Accessor("firstPersonHandsAndItems")
    FirstPersonHandsAndItems scenecraft$getFirstPersonHandsAndItems();
}
