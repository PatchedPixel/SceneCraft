package id.patchedpixel.scenecraft.mixin.render;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Window.class)
public interface MainWindowAccessor {
    @Accessor
    int getFramebufferWidth();
    @Accessor
    void setFramebufferWidth(int value);
    @Accessor
    int getFramebufferHeight();
    @Accessor
    void setFramebufferHeight(int value);
}
