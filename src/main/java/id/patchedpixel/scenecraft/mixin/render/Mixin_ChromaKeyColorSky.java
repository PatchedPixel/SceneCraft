package id.patchedpixel.scenecraft.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.gui.utils.color.ReadableColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Instead of rendering the normal sky, clears the screen with a uniform color for use with chroma keying.
 */
@Mixin(LevelRenderer.class)
public abstract class Mixin_ChromaKeyColorSky {
    @Inject(
            method = "lambda$addSkyPass$0",
            at = @At("HEAD"),
            cancellable = true)
    static
    private void chromaKeyingSky(CallbackInfo ci) {
        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) Minecraft.getInstance().gameRenderer).replayModRender_getHandler();
        if (handler != null) {
            ReadableColor color = handler.getSettings().getChromaKeyingColor();
            if (color != null) {
                RenderSystem.getDevice().createCommandEncoder().clearColorTexture(
                        Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTexture(),
                        new org.joml.Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f)
                );
                ci.cancel();
            }
        }
    }
}
