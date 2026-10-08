package id.patchedpixel.scenecraft.render.hooks;

import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.core.events.PreRenderHandCallback;
import id.patchedpixel.scenecraft.core.versions.MCVer;
import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.render.Setting;
import id.patchedpixel.scenecraft.render.capturer.CaptureData;
import id.patchedpixel.scenecraft.render.capturer.RenderInfo;
import id.patchedpixel.scenecraft.render.capturer.WorldRenderer;
import id.patchedpixel.scenecraft.mixin.render.GameRendererAccessor;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import id.patchedpixel.scenecraft.core.events.PostRenderCallback;
import id.patchedpixel.scenecraft.core.events.PreRenderCallback;
import net.minecraft.util.Util;

import java.io.IOException;

public class EntityRendererHandler extends EventRegistrations implements WorldRenderer {
    public final Minecraft mc = MCVer.getMinecraft();

    protected final RenderSettings settings;

    private final RenderInfo renderInfo;

    public CaptureData data;

    public boolean omnidirectional;

    private final long startTime;

    private long fakeFinishTimeNano;

    public EntityRendererHandler(RenderSettings settings, RenderInfo renderInfo) {
        this.settings = settings;
        this.renderInfo = renderInfo;

        this.startTime = Util.getNanos();

        on(PreRenderHandCallback.EVENT, () -> omnidirectional);

        ((IEntityRenderer) mc.gameRenderer).replayModRender_setHandler(this);
        register();
    }

    @Override
    public void renderWorld(final float partialTicks, CaptureData data) {
        this.data = data;
        long offsetMillis;
        if (SceneCraft.instance.getSettingsRegistry().get(Setting.FRAME_TIME_FROM_WORLD_TIME)) {
            offsetMillis = SceneCraftReplay.instance.getReplayHandler().getReplaySender().currentTimeStamp();
        } else {
            offsetMillis = renderInfo.getFramesDone() * 1_000L / settings.getFramesPerSecond();
        }
        long frameStartTimeNano = startTime + offsetMillis * 1_000_000L;
        renderWorld(partialTicks, frameStartTimeNano);
    }

    public void renderWorld(float partialTicks, long finishTimeNano) {
        fakeFinishTimeNano = finishTimeNano;
        PreRenderCallback.EVENT.invoker().preRender();

        if (mc.level != null && mc.player != null) {
            GameRendererAccessor gameRenderer = (GameRendererAccessor) mc.gameRenderer;
            Screen orgScreen = mc.gui.screen();
            boolean orgPauseOnLostFocus = mc.options.pauseOnLostFocus;
            boolean orgHudIsHidden = mc.gui.hud.isHidden();
            try {
                ((id.patchedpixel.scenecraft.core.mixin.GuiAccessor) mc.gui).replaymod$setScreen(null);
                mc.options.pauseOnLostFocus = false; // do not want the pause menu to open if the window is unfocused
                if (omnidirectional) {
                    // makes no sense, we wouldn't even know where to put it
                    mc.gameRenderer.mainCamera().enablePanoramicMode();
                }
                if (!orgHudIsHidden) mc.gui.hud.toggle();

                mc.gameRenderer.update(mc.getDeltaTracker());
                mc.gameRenderer.extract(mc.getDeltaTracker(), true);
                mc.gameRenderer.render(mc.getDeltaTracker(), true);
            } finally {
                ((id.patchedpixel.scenecraft.core.mixin.GuiAccessor) mc.gui).replaymod$setScreen(orgScreen);
                mc.options.pauseOnLostFocus = orgPauseOnLostFocus;
                mc.gameRenderer.mainCamera().disablePanoramicMode();
                if (!orgHudIsHidden) mc.gui.hud.toggle();
            }
        }

        PostRenderCallback.EVENT.invoker().postRender();
    }

    @Override
    public void close() throws IOException {
        ((IEntityRenderer) mc.gameRenderer).replayModRender_setHandler(null);
        unregister();
    }

    @Override
    public void setOmnidirectional(boolean omnidirectional) {
        this.omnidirectional = omnidirectional;
    }

    public RenderSettings getSettings() {
        return this.settings;
    }

    public RenderInfo getRenderInfo() {
        return this.renderInfo;
    }

    public long getFakeFinishTimeNano() {
        return fakeFinishTimeNano;
    }

    public interface IEntityRenderer {
        void replayModRender_setHandler(EntityRendererHandler handler);
        EntityRendererHandler replayModRender_getHandler();
    }
}
