package id.patchedpixel.scenecraft.mixin.replay;

import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.3 moved the equip-animation state out of ItemInHandRenderer into {@link FirstPersonHandsAndItems}
 * (owned by LocalPlayer). Field names are carried over from the old renderer.
 */
@Mixin(FirstPersonHandsAndItems.class)
public interface FirstPersonRendererAccessor {
    @Accessor("mainHandItem")
    void setItemStackMainHand(ItemStack value);
    @Accessor("offHandItem")
    void setItemStackOffHand(ItemStack value);
    @Accessor("mainHandHeight")
    void setEquippedProgressMainHand(float value);
    @Accessor("oMainHandHeight")
    void setPrevEquippedProgressMainHand(float value);
    @Accessor("offHandHeight")
    void setEquippedProgressOffHand(float value);
    @Accessor("oOffHandHeight")
    void setPrevEquippedProgressOffHand(float value);
}
