package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.FramebufferDelegateHolder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class Mixin_UseGuiFramebufferDuringGuiRendering implements FramebufferDelegateHolder {
    @Unique
    private RenderTarget framebufferDelegate;

    @Override
    public void setFramebufferDelegate(RenderTarget framebuffer) {
        this.framebufferDelegate = framebuffer;
    }

    @Inject(method = "mainRenderTarget", at = @At("HEAD"), cancellable = true)
    private void useGuiFramebuffer(CallbackInfoReturnable<RenderTarget> ci) {
        RenderTarget delegate = this.framebufferDelegate;
        if (delegate != null) {
            ci.setReturnValue(delegate);
        }
    }
}
