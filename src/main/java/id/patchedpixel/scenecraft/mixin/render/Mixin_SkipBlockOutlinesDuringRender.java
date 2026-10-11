package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(LevelRenderer.class)
public abstract class Mixin_SkipBlockOutlinesDuringRender {
    @Shadow @Final private GameRenderer gameRenderer;

    @Inject(method = "submitHitOutline", at = @At("HEAD"), cancellable = true)
    private void replayModRender_drawSelectionBox(CallbackInfo ci) {
        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) this.gameRenderer).sceneCraftRender_getHandler();
        if (handler != null) {
            ci.cancel();
        }
    }
}
