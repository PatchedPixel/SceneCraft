package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class Mixin_SkipHudDuringRender {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void replayModRender_skipHudDuringRender(CallbackInfo ci) {
        if (((EntityRendererHandler.IEntityRenderer) Minecraft.getInstance().gameRenderer).sceneCraftRender_getHandler() != null) {
            ci.cancel();
        }
    }
}
