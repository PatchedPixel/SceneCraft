package id.patchedpixel.scenecraft.mixin.record;

import id.patchedpixel.scenecraft.replay.InputReplayTimer;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHelper {
    @Shadow
    private boolean mouseGrabbed;

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    private void noGrab(CallbackInfo ci) {
        // Used to be provided by Forge for 1.12.2 and below
        if (Boolean.valueOf(System.getProperty("fml.noGrab", "false"))) {
            this.mouseGrabbed = true;
            ci.cancel();
        }
    }

    @Inject(method = "onScroll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void handleReplayModScroll(
            long _p0, double _p1, double _p2,
            CallbackInfo ci,
            boolean discreteScroll,
            double sensitivity,
            double xOffsetAccumulated,
            double yOffsetAccumulated
    ) {
        if (SceneCraftReplay.instance.getReplayHandler() != null) {
            InputReplayTimer.handleScroll((int) (yOffsetAccumulated * 120));
            ci.cancel();
        }
    }
}
