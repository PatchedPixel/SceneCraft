package id.patchedpixel.scenecraft.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import id.patchedpixel.scenecraft.gui.versions.ScreenExt;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(KeyboardHandler.class)
public class Mixin_PassEvents_HandleKeys {
    @ModifyExpressionValue(
            method = "keyPress",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;screen()Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=keyPressed event handler"))
    )
    private Screen doesScreenPassEvents(Screen screen) {
        if (screen instanceof ScreenExt ext && ext.doesPassEvents()) {
            screen = null;
        }

        return screen;
    }
}
