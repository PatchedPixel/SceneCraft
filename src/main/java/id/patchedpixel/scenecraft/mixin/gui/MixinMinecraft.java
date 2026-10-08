package id.patchedpixel.scenecraft.mixin.gui;

import id.patchedpixel.scenecraft.gui.versions.callbacks.OpenGuiScreenCallback;
import id.patchedpixel.scenecraft.gui.versions.callbacks.PreTickCallback;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public abstract class MixinMinecraft {
    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        PreTickCallback.EVENT.invoker().preTick();
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;"))
    private void openGuiScreen(Screen newGuiScreen, CallbackInfo ci) {
        OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(newGuiScreen);
    }
}
