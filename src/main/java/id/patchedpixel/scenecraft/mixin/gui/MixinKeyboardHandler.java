package id.patchedpixel.scenecraft.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import id.patchedpixel.scenecraft.gui.function.CharInput;
import id.patchedpixel.scenecraft.gui.function.KeyInput;
import id.patchedpixel.scenecraft.gui.versions.ScreenExt;
import id.patchedpixel.scenecraft.gui.versions.callbacks.KeyboardCallback;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @WrapOperation(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
    private boolean keyPressed(Screen screen, net.minecraft.client.input.KeyEvent mcKeyInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().keyPressed(new KeyInput(mcKeyInput))) {
            return true;
        }
        return original.call(screen, mcKeyInput);
    }

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

    @WrapOperation(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(Lnet/minecraft/client/input/KeyEvent;)Z"))
    private boolean keyReleased(Screen screen, net.minecraft.client.input.KeyEvent mcKeyInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().keyReleased(new KeyInput(mcKeyInput))) {
            return true;
        }
        return original.call(screen, mcKeyInput);
    }

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
    private boolean charTyped(Screen screen, net.minecraft.client.input.CharacterEvent mcCharInput, Operation<Boolean> original) {
        if (KeyboardCallback.EVENT.invoker().charTyped(new CharInput(mcCharInput))) {
            return true;
        }
        return original.call(screen, mcCharInput);
    }
}
