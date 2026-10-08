package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.replay.camera.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinPlayerControllerMP {

    @Shadow
    private Minecraft minecraft;

    @Inject(method = "isSpectator", at=@At("HEAD"), cancellable = true)
    private void replayModReplay_isSpectator(CallbackInfoReturnable<Boolean> ci) {
        if (this.minecraft.player instanceof CameraEntity) { // this check should in theory not be required
            ci.setReturnValue(this.minecraft.player.isSpectator());
        }
    }

}
