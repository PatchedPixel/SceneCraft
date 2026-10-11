package id.patchedpixel.scenecraft.record.handler;

import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.core.SettingsRegistry;
import id.patchedpixel.scenecraft.core.gui.GuiReplayButton;
import id.patchedpixel.scenecraft.record.ServerInfoExt;
import id.patchedpixel.scenecraft.record.Setting;
import id.patchedpixel.scenecraft.mixin.record.AddServerScreenAccessor;
import id.patchedpixel.scenecraft.gui.container.GuiScreen;
import id.patchedpixel.scenecraft.gui.container.VanillaGuiScreen;
import id.patchedpixel.scenecraft.gui.element.GuiButton;
import id.patchedpixel.scenecraft.gui.element.GuiCheckbox;
import id.patchedpixel.scenecraft.gui.element.GuiToggleButton;
import id.patchedpixel.scenecraft.gui.layout.CustomLayout;
import id.patchedpixel.scenecraft.gui.popup.GuiInfoPopup;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;
import id.patchedpixel.scenecraft.gui.versions.callbacks.InitScreenCallback;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;

public class GuiHandler extends EventRegistrations {
    private final SceneCraft mod;

    public GuiHandler(SceneCraft mod) {
        this.mod = mod;
    }

    { on(InitScreenCallback.EVENT, (screen, buttons) -> onGuiInit(screen)); }
    private void onGuiInit(net.minecraft.client.gui.screens.Screen gui) {
        if (gui instanceof SelectWorldScreen || gui instanceof JoinMultiplayerScreen) {
            boolean sp = gui instanceof SelectWorldScreen;
            SettingsRegistry settingsRegistry = mod.getSettingsRegistry();
            Setting<Boolean> setting = sp ? Setting.RECORD_SINGLEPLAYER : Setting.RECORD_SERVER;

            GuiCheckbox recordingCheckbox = new GuiCheckbox()
                    .setI18nLabel("scenecraft.gui.settings.record" + (sp ? "singleplayer" : "server"))
                    .setChecked(settingsRegistry.get(setting));
            recordingCheckbox.onClick(() -> {
                settingsRegistry.set(setting, recordingCheckbox.isChecked());
                settingsRegistry.save();
            });

            VanillaGuiScreen vanillaGui = VanillaGuiScreen.wrap(gui);
            vanillaGui.setLayout(new CustomLayout<GuiScreen>(vanillaGui.getLayout()) {
                @Override
                protected void layout(GuiScreen container, int width, int height) {
                    //size(recordingCheckbox, 200, 20);
                    pos(recordingCheckbox, width - width(recordingCheckbox) - 5, 5);
                }
            }).addElements(null, recordingCheckbox);
        }

        if (gui instanceof ManageServerScreen) {
            VanillaGuiScreen vanillaGui = VanillaGuiScreen.wrap(gui);
            GuiButton replayButton = new GuiReplayButton().onClick(() -> {
                ServerData serverInfo = ((AddServerScreenAccessor) gui).getServer();
                ServerInfoExt serverInfoExt = ServerInfoExt.from(serverInfo);
                Boolean state = serverInfoExt.getAutoRecording();
                GuiToggleButton<String> autoRecording = new GuiToggleButton<String>()
                        .setI18nLabel("scenecraft.gui.settings.autostartrecording")
                        .setValues(
                                I18n.get("scenecraft.gui.settings.default"),
                                I18n.get("options.off"),
                                I18n.get("options.on")
                        )
                        .setSelected(state == null ? 0 : state ? 2 : 1);
                autoRecording.onClick(() -> {
                    int selected = autoRecording.getSelected();
                    serverInfoExt.setAutoRecording(selected == 0 ? null : selected == 2);
                });
                GuiInfoPopup.open(vanillaGui, autoRecording);
            });
            vanillaGui.setLayout(new CustomLayout<GuiScreen>(vanillaGui.getLayout()) {
                @Override
                protected void layout(GuiScreen container, int width, int height) {
                    size(replayButton, 20, 20);
                    pos(replayButton, width - width(replayButton) - 5, 5);
                }
            }).addElements(null, replayButton);
        }
    }
}
