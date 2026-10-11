package id.patchedpixel.scenecraft.mixin.compat.shaders;

import id.patchedpixel.scenecraft.compat.shaders.ShaderReflection;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinShaderEntityRenderer {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void replayModCompat_updateShaderFrameTimeCounter(CallbackInfo ignore) {
        if (SceneCraftReplay.instance.getReplayHandler() == null) return;
        if (ShaderReflection.shaders_frameTimeCounter == null) return;

        ReplayHandler replayHandler = SceneCraftReplay.instance.getReplayHandler();
        float timestamp = replayHandler.getReplaySender().currentTimeStamp() / 1000f % 3600f;
        try {
            ShaderReflection.shaders_frameTimeCounter.set(null, timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
