package id.patchedpixel.scenecraft.extras;

import id.patchedpixel.scenecraft.core.SceneCraft;
import com.mojang.blaze3d.platform.InputConstants;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import id.patchedpixel.scenecraft.replay.events.ReplayOpenedCallback;
import id.patchedpixel.scenecraft.replay.gui.overlay.GuiReplayOverlay;
import id.patchedpixel.scenecraft.gui.element.GuiImage;
import id.patchedpixel.scenecraft.gui.layout.HorizontalLayout;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;

public class QuickMode extends EventRegistrations implements Extra {
    private SceneCraftReplay module;

    private final GuiImage indicator = new GuiImage().setTexture(SceneCraft.TEXTURE, 40, 100, 16, 16).setSize(16, 16);

    @Override
    public void register(final SceneCraft mod) {
        this.module = SceneCraftReplay.instance;

        mod.getKeyBindingRegistry().registerKeyBinding("scenecraft.input.quickmode", InputConstants.KEY_Q, () -> {
            ReplayHandler replayHandler = module.getReplayHandler();
            if (replayHandler == null) {
                return;
            }
            replayHandler.getReplaySender().setSyncModeAndWait();
            mod.runLaterWithoutLock(() -> {
                replayHandler.ensureQuickModeInitialized(() -> {
                    boolean enabled = !replayHandler.isQuickMode();
                    updateIndicator(replayHandler.getOverlay(), enabled);
                    replayHandler.setQuickMode(enabled);
                    replayHandler.getReplaySender().setAsyncMode(true);
                });
            });
        }, true);

        register();
    }

    {
        on(ReplayOpenedCallback.EVENT, replayHandler -> updateIndicator(replayHandler.getOverlay(), replayHandler.isQuickMode()));
    }

    private void updateIndicator(GuiReplayOverlay overlay, boolean enabled) {
        if (enabled) {
            overlay.statusIndicatorPanel.addElements(new HorizontalLayout.Data(1), indicator);
        } else {
            overlay.statusIndicatorPanel.removeElement(indicator);
        }
    }
}
