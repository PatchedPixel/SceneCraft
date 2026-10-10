package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.replay.ReplayModReplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.Util;

@Mixin(net.minecraft.client.renderer.rendertype.TextureTransform.class)
public class MixinRenderItem {
    @Redirect(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMillis()J"))
    private static long getEnchantmentTime() {
        ReplayHandler replayHandler = ReplayModReplay.instance.getReplayHandler();
        if (replayHandler != null) {
            return replayHandler.getReplaySender().currentTimeStamp();
        }
        return Util.getMillis();
    }
}
