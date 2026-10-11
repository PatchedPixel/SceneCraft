package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public abstract class MixinEntityRenderer implements EntityRendererHandler.IEntityRenderer {
    private EntityRendererHandler sceneCraftRender_handler;

    @Override
    public void sceneCraftRender_setHandler(EntityRendererHandler handler) {
        this.sceneCraftRender_handler = handler;
    }

    @Override
    public EntityRendererHandler sceneCraftRender_getHandler() {
        return sceneCraftRender_handler;
    }
}
