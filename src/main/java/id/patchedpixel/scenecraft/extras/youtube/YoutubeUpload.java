package id.patchedpixel.scenecraft.extras.youtube;

import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.extras.Extra;
import id.patchedpixel.scenecraft.render.gui.GuiRenderingDone;
import id.patchedpixel.scenecraft.gui.container.AbstractGuiScreen;
import id.patchedpixel.scenecraft.gui.element.GuiButton;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;
import id.patchedpixel.scenecraft.gui.versions.callbacks.InitScreenCallback;
import net.minecraft.client.gui.screens.Screen;

// The YouTube uploader is not working anymore
@Deprecated(forRemoval = true)
public class YoutubeUpload extends EventRegistrations implements Extra {
    @Override
    public void register(SceneCraft mod) {
        register();
    }

    { on(InitScreenCallback.EVENT, ((screen, buttons) -> onGuiOpen(screen))); }
    private void onGuiOpen(Screen vanillaGui) {
        AbstractGuiScreen<?> abstractScreen = id.patchedpixel.scenecraft.gui.container.GuiScreen.from(vanillaGui);
        if (abstractScreen instanceof GuiRenderingDone) {
            GuiRenderingDone gui = (GuiRenderingDone) abstractScreen;
            // Check if there already is a youtube button
            if (gui.actionsPanel.getChildren().stream().anyMatch(it -> it instanceof YoutubeButton)) {
                return; // Button already added
            }
            // Add the Upload to YouTube button to actions panel
            gui.actionsPanel.addElements(null,
                    new YoutubeButton().onClick(() ->
                            new GuiYoutubeUpload(gui, gui.videoFile, gui.videoFrames, gui.settings).display()
                    ).setSize(200, 20).setI18nLabel("scenecraft.gui.youtubeupload"));
        }
    }

    private static class YoutubeButton extends GuiButton {}
}
