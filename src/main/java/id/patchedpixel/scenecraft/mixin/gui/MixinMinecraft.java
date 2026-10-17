package id.patchedpixel.scenecraft.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import id.patchedpixel.scenecraft.gui.versions.ScreenExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    /**
     * Minecraft#tick checks Gui#screen() twice between the "gui" and "gameRenderer" profiler sections:
     * once to block attacks while a screen is open (missTime) and once to decide whether keybinds are handled.
     * Hiding the screen from both lets screens that pass events keep the game controllable.
     */
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;screen()Lnet/minecraft/client/gui/screens/Screen;"),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=gui"),
                    to = @At(value = "CONSTANT", args = "stringValue=gameRenderer")
            )
    )
    private Screen doesScreenPassEvents(Screen screen) {
        if (screen instanceof ScreenExt ext && ext.doesPassEvents()) {
            return null;
        }

        return screen;
    }
}
