package id.patchedpixel.scenecraft.mixin.gui;

import id.patchedpixel.scenecraft.gui.versions.callbacks.RenderHudCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class Mixin_RenderHudCallback {
    // The demo overlay call no longer exists in Hud#extractRenderState. All HUD layers are now
    // rendered through the NeoForge layer manager, so fire the callback right after it has finished.
    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        RenderHudCallback.EVENT.invoker().renderHud(graphics, partialTicks);
    }
}
