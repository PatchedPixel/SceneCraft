package id.patchedpixel.scenecraft.compat.shaders;

import id.patchedpixel.scenecraft.core.events.PreRenderCallback;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;
import net.minecraft.client.Minecraft;

import java.lang.reflect.InvocationTargetException;

public class ShaderBeginRender extends EventRegistrations {
    private final Minecraft mc = Minecraft.getInstance();

    /**
     *  Invokes Shaders#beginRender when rendering a video,
     *  as this would usually get called by EntityRenderer#renderWorld,
     *  which we're not calling during rendering.
     */
    { on(PreRenderCallback.EVENT, this::onRenderTickStart); }
    private void onRenderTickStart() {
        if (ShaderReflection.shaders_beginRender == null) return;
        if (ShaderReflection.config_isShaders == null) return;

        try {
            // check if video is being rendered
            if (((EntityRendererHandler.IEntityRenderer) mc.gameRenderer).replayModRender_getHandler() == null)
                return;

            // check if Shaders are enabled
            if (!(boolean) (ShaderReflection.config_isShaders.invoke(null))) return;

            ShaderReflection.shaders_beginRender.invoke(null, mc,
                    mc.gameRenderer.mainCamera(),
                    mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), 0);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
