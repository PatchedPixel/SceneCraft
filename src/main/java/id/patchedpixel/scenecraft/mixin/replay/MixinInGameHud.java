package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.replay.events.RenderHotbarCallback;
import id.patchedpixel.scenecraft.replay.events.RenderSpectatorCrosshairCallback;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hud.class)
public class MixinInGameHud {
    @Inject(method = "canRenderCrosshairForSpectator", at = @At("HEAD"), cancellable = true)
    private void shouldRenderSpectatorCrosshair(CallbackInfoReturnable<Boolean> ci) {
        Boolean state = RenderSpectatorCrosshairCallback.EVENT.invoker().shouldRenderSpectatorCrosshair();
        if (state != null) {
            ci.setReturnValue(state);
        }
    }

    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    private void shouldRenderHotbar(CallbackInfo ci) {
        Boolean state = RenderHotbarCallback.EVENT.invoker().shouldRenderHotbar();
        if (state == Boolean.FALSE) {
            ci.cancel();
        }
    }
}
