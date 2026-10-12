package id.patchedpixel.scenecraft.render.hooks;

import net.minecraft.client.renderer.LevelRenderer;

public interface IForceChunkLoading {
    void sceneCraftRender_setHook(ForceChunkLoadingHook hook);

    static IForceChunkLoading from(LevelRenderer worldRenderer) {
        return (IForceChunkLoading) worldRenderer;
    }
}
