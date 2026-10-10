package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.extras.advancedscreenshots.AdvancedScreenshots;
import id.patchedpixel.scenecraft.replay.ReplayModReplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Screenshot;

@Mixin(Screenshot.class)
public abstract class MixinKeyboardListener {
    @Inject(method = "grab(Lnet/minecraft/client/Minecraft;Z)V", at = @At("HEAD"), cancellable = true)
    static
    private void takeScreenshot(CallbackInfo ci) {
        if (ReplayModReplay.instance.getReplayHandler() != null) {
            AdvancedScreenshots.take();
            ci.cancel();
        }
    }
}
