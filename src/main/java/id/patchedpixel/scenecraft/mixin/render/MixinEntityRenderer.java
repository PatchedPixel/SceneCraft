package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public abstract class MixinEntityRenderer implements EntityRendererHandler.IEntityRenderer {
    private EntityRendererHandler replayModRender_handler;

    @Override
    public void replayModRender_setHandler(EntityRendererHandler handler) {
        this.replayModRender_handler = handler;
    }

    @Override
    public EntityRendererHandler replayModRender_getHandler() {
        return replayModRender_handler;
    }
}
