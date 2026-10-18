package id.patchedpixel.scenecraft.render.gui;

import id.patchedpixel.scenecraft.gui.container.GuiPanel;
import id.patchedpixel.scenecraft.gui.container.GuiScreen;
import id.patchedpixel.scenecraft.gui.element.GuiButton;
import id.patchedpixel.scenecraft.gui.element.GuiLabel;
import id.patchedpixel.scenecraft.gui.layout.HorizontalLayout;
import id.patchedpixel.scenecraft.gui.layout.VerticalLayout;

import java.net.URI;

import static id.patchedpixel.scenecraft.core.versions.MCVer.openURL;
import static id.patchedpixel.scenecraft.core.versions.MCVer.setClipboardString;

public class GuiNoFfmpeg extends GuiScreen {
    private static final String LINK = "https://www.replaymod.com/docs/#installing-ffmpeg";

    private final GuiLabel message = new GuiLabel()
            .setI18nText("scenecraft.gui.rendering.error.message");
    private final GuiLabel link = new GuiLabel()
            .setText(LINK);
    private final GuiButton openLinkButton = new GuiButton()
            .setI18nLabel("chat.link.open")
            .setSize(100, 20)
            .onClick(() -> openURL(URI.create(LINK)));
    private final GuiButton copyToClipboardButton = new GuiButton()
            .setI18nLabel("chat.copy")
            .setSize(100, 20)
            .onClick(() -> setClipboardString(LINK));
    private final GuiButton backButton = new GuiButton()
            .setI18nLabel("gui.back")
            .setSize(100, 20);
    private final GuiPanel buttons = new GuiPanel()
            .setLayout(new HorizontalLayout(HorizontalLayout.Alignment.CENTER).setSpacing(4))
            .addElements(null, openLinkButton, copyToClipboardButton, backButton);

    {
        setBackground(Background.DIRT);
        setTitle(new GuiLabel().setI18nText("scenecraft.gui.rendering.error.title"));
        setLayout(new VerticalLayout(VerticalLayout.Alignment.CENTER).setSpacing(30));
        addElements(new VerticalLayout.Data(0.5), message, link, buttons);
    }

    public GuiNoFfmpeg(Runnable goBack) {
        backButton.onClick(goBack);
    }
}
