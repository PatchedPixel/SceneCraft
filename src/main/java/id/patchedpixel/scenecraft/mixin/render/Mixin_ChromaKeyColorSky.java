package id.patchedpixel.scenecraft.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.systems.RenderSystem;
import id.patchedpixel.scenecraft.gui.utils.color.ReadableColor;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Instead of rendering the normal sky, clears the screen with a uniform color for use with chroma keying.
 * <p>
 * Wraps the {@code FramePass.executes(Runnable)} call in {@code addSkyPass} instead of targeting the synthetic
 * lambda directly (its name/index changes whenever lambdas are added to {@link LevelRenderer}).
 */
@Mixin(LevelRenderer.class)
public abstract class Mixin_ChromaKeyColorSky {
    @WrapOperation(
            method = "addSkyPass",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FramePass;executes(Ljava/lang/Runnable;)V")
    )
    private void chromaKeyingSky(FramePass pass, Runnable task, Operation<Void> original) {
        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) Minecraft.getInstance().gameRenderer).sceneCraftRender_getHandler();
        if (handler != null) {
            ReadableColor color = handler.getSettings().getChromaKeyingColor();
            if (color != null) {
                original.call(pass, (Runnable) () -> RenderSystem.getDevice().createCommandEncoder().clearColorTexture(
                        Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTexture(),
                        new org.joml.Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f)
                ));
                return;
            }
        }
        original.call(pass, task);
    }
}
