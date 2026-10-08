package id.patchedpixel.scenecraft.mixin.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.textures.GpuTexture;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class Mixin_PreserveDepthDuringHandRendering {
    @WrapWithCondition(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/CommandEncoder;clearDepthTexture(Lcom/mojang/renderpearl/api/textures/GpuTexture;D)V")
    )
    private boolean replayModRender_skipClearWhenRecordingDepth(CommandEncoder instance, GpuTexture texture, double v) {
        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) this).replayModRender_getHandler();
        return handler == null || !handler.getSettings().isDepthMap();
    }
}
