package id.patchedpixel.scenecraft.replay.gui.screen;

import com.google.common.util.concurrent.SettableFuture;
import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.render.gui.GuiRenderQueue;
import id.patchedpixel.scenecraft.render.rendering.VideoRenderer;
import id.patchedpixel.scenecraft.render.utils.RenderJob;
import id.patchedpixel.scenecraft.gui.GuiRenderer;
import id.patchedpixel.scenecraft.gui.RenderInfo;
import id.patchedpixel.scenecraft.gui.function.CharHandler;
import id.patchedpixel.scenecraft.gui.function.CharInput;
import id.patchedpixel.scenecraft.gui.function.Click;
import id.patchedpixel.scenecraft.gui.function.KeyHandler;
import id.patchedpixel.scenecraft.gui.function.KeyInput;
import id.patchedpixel.scenecraft.gui.versions.Image;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import net.minecraft.client.gui.Font;
import net.minecraft.ChatFormatting;
import id.patchedpixel.scenecraft.core.SettingsRegistry;
import id.patchedpixel.scenecraft.core.gui.GuiReplaySettings;
import id.patchedpixel.scenecraft.core.utils.Utils;
import id.patchedpixel.scenecraft.core.versions.MCVer;
import id.patchedpixel.scenecraft.core.versions.MCVer.Keyboard;
import id.patchedpixel.scenecraft.replay.Setting;
import id.patchedpixel.scenecraft.scenestudio.replay.ReplayFile;
import id.patchedpixel.scenecraft.scenestudio.replay.ReplayMetaData;
import id.patchedpixel.scenecraft.gui.container.AbstractGuiContainer;
import id.patchedpixel.scenecraft.gui.container.GuiContainer;
import id.patchedpixel.scenecraft.gui.container.GuiPanel;
import id.patchedpixel.scenecraft.gui.container.GuiScreen;
import id.patchedpixel.scenecraft.gui.element.*;
import id.patchedpixel.scenecraft.gui.element.advanced.AbstractGuiResourceLoadingList;
import id.patchedpixel.scenecraft.gui.layout.CustomLayout;
import id.patchedpixel.scenecraft.gui.layout.HorizontalLayout;
import id.patchedpixel.scenecraft.gui.layout.VerticalLayout;
import id.patchedpixel.scenecraft.gui.popup.AbstractGuiPopup;
import id.patchedpixel.scenecraft.gui.popup.GuiYesNoPopup;
import id.patchedpixel.scenecraft.gui.utils.Colors;
import id.patchedpixel.scenecraft.gui.utils.Consumer;
import id.patchedpixel.scenecraft.gui.utils.geometry.Dimension;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static id.patchedpixel.scenecraft.replay.SceneCraftReplay.LOGGER;
import static id.patchedpixel.scenecraft.gui.versions.MCVer.getFontRenderer;

public class GuiReplayViewer extends GuiScreen {
    private final SceneCraftReplay mod;

    public final GuiReplayList list = new GuiReplayList(this).onSelectionChanged(this::updateButtons).onSelectionDoubleClicked(() -> {
        if (this.loadButton.isEnabled()) {
            this.loadButton.onClick(new Click(-1, -1, 0, 0));
        }
    });

    public final GuiButton loadButton = new GuiButton().onClick(new Runnable() {
        private boolean loading = false;

        @Override
        public void run() {
            // Prevent the player from opening the replay twice at the same time
            if (loading) {
                return;
            }
            loading = true;
            loadButton.setDisabled(); // visual hint

            List<GuiReplayEntry> selected = list.getSelected();
            if (selected.size() == 1) {
                File file = selected.get(0).file;
                LOGGER.info("Opening replay in viewer: " + file);
                try {
                    mod.startReplay(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Iterator<Pair<File, List<RenderJob>>> replays = selected.stream()
                        .filter(it -> !it.renderQueue.isEmpty())
                        .map(it -> Pair.of(it.file, it.renderQueue))
                        .iterator();
                GuiRenderQueue.processMultipleReplays(GuiReplayViewer.this, mod, replays, () -> {
                    loading = false;
                    updateButtons();
                    display();
                });
            }
        }
    }).setSize(150, 20);

    public final GuiButton folderButton = new GuiButton().onClick(new Runnable() {
        @Override
        public void run() {
            try {
                File folder = mod.getCore().folders.getReplayFolder().toFile();

                MCVer.openFile(folder);
            } catch (IOException e) {
                mod.getLogger().error("Cannot open file", e);
            }
        }
    }).setSize(150, 20).setI18nLabel("scenecraft.gui.viewer.replayfolder");

    public final GuiButton renameButton = new GuiButton().onClick(new Runnable() {
        @Override
        public void run() {
            final Path path = list.getSelected().get(0).file.toPath();
            String name = Utils.fileNameToReplayName(path.getFileName().toString());
            final GuiTextField nameField = new GuiTextField().setSize(200, 20).setFocused(true).setText(name);
            final GuiYesNoPopup popup = GuiYesNoPopup.open(GuiReplayViewer.this,
                    new GuiLabel().setI18nText("scenecraft.gui.viewer.rename.name").setColor(Colors.BLACK),
                    nameField
            ).setYesI18nLabel("scenecraft.gui.rename").setNoI18nLabel("scenecraft.gui.cancel");
            ((VerticalLayout) popup.getInfo().getLayout()).setSpacing(7);
            nameField.onEnter(new Runnable() {
                @Override
                public void run() {
                    if (popup.getYesButton().isEnabled()) {
                        popup.getYesButton().onClick(new Click(-1, -1, 0, 0));
                    }
                }
            }).onTextChanged(obj -> {
                popup.getYesButton().setEnabled(!nameField.getText().isEmpty()
                        && Files.notExists(Utils.replayNameToPath(path.getParent(), nameField.getText())));
            });
            popup.onAccept(() -> {
                // Sanitize their input
                String newName = nameField.getText().trim();
                // This file is what they want
                Path targetPath = Utils.replayNameToPath(path.getParent(), newName);
                try {
                    // Finally, try to move it
                    Files.move(path, targetPath);
                } catch (IOException e) {
                    // We failed (might also be their OS)
                    e.printStackTrace();
                    getMinecraft().gui.setScreen(new AlertScreen(
                            GuiReplayViewer.this::display,
                            net.minecraft.network.chat.Component.translatable("scenecraft.gui.viewer.delete.failed1"),
                            net.minecraft.network.chat.Component.translatable("scenecraft.gui.viewer.delete.failed2")
                    ));

                    return;
                }
                list.load();
            });
        }
    }).setSize(73, 20).setI18nLabel("scenecraft.gui.rename").setDisabled();
    public final GuiButton deleteButton = new GuiButton().onClick(() -> {
        for (GuiReplayEntry entry : list.getSelected()) {
            String name = entry.name.getText();
            GuiYesNoPopup.open(GuiReplayViewer.this,
                    new GuiLabel().setI18nText("scenecraft.gui.viewer.delete.linea").setColor(Colors.BLACK),
                    new GuiLabel().setI18nText("scenecraft.gui.viewer.delete.lineb", name + ChatFormatting.RESET).setColor(Colors.BLACK)
            ).setYesI18nLabel("scenecraft.gui.delete").setNoI18nLabel("scenecraft.gui.cancel").onAccept(() -> {
                try {
                    FileUtils.forceDelete(entry.file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.load();
            });
        }
    }).setSize(73, 20).setI18nLabel("scenecraft.gui.delete").setDisabled();

    public final GuiButton settingsButton = new GuiButton(this)
            .setSize(20, 20)
            .setTexture(SceneCraft.TEXTURE, SceneCraft.TEXTURE_SIZE).setSpriteUV(20, 0)
            .setTooltip(new GuiTooltip().setI18nText("scenecraft.gui.settings"))
            .onClick(() -> new GuiReplaySettings(toMinecraft(), getMod().getCore().getSettingsRegistry()).display());

    public final GuiButton cancelButton = new GuiButton().onClick(new Runnable() {
        @Override
        public void run() {
            getMinecraft().gui.setScreen(null);
        }
    }).setSize(73, 20).setI18nLabel("scenecraft.gui.cancel");

    public final List<GuiButton> replaySpecificButtons = new ArrayList<>();
    { replaySpecificButtons.addAll(Arrays.asList(renameButton)); }
    public final GuiPanel editorButton = new GuiPanel();

    public final GuiPanel upperButtonPanel = new GuiPanel().setLayout(new HorizontalLayout().setSpacing(5))
            .addElements(null, loadButton);
    public final GuiPanel lowerButtonPanel = new GuiPanel().setLayout(new HorizontalLayout().setSpacing(5))
            .addElements(null, renameButton, deleteButton, editorButton, cancelButton);
    public final GuiPanel buttonPanel = new GuiPanel(this).setLayout(new VerticalLayout().setSpacing(5))
            .addElements(null, upperButtonPanel, lowerButtonPanel);

    public GuiReplayViewer(SceneCraftReplay mod) {
        this.mod = mod;

        try {
            list.setFolder(mod.getCore().folders.getReplayFolder().toFile());
        } catch (IOException e) {
            throw new ReportedException(CrashReport.forThrowable(e, "Getting replay folder"));
        }

        setTitle(new GuiLabel().setI18nText("scenecraft.gui.replayviewer"));

        setLayout(new CustomLayout<GuiScreen>() {
            @Override
            protected void layout(GuiScreen container, int width, int height) {
                pos(buttonPanel, width / 2 - width(buttonPanel) / 2, height - 10 - height(buttonPanel));

                pos(list, 0, 30);
                size(list, width, y(buttonPanel) - 10 - y(list));

                pos(settingsButton, width - width(settingsButton) - 5, 5);
            }
        });

        updateButtons();
    }

    public SceneCraftReplay getMod() {
        return mod;
    }

    private void updateButtons() {
        List<GuiReplayEntry> selected = list.getSelected();
        int count = selected.size();

        replaySpecificButtons.forEach(b -> b.setEnabled(count == 1));
        deleteButton.setEnabled(count > 0);
        if (count > 1) {
            Set<RenderJob> jobs = selected.stream().flatMap(entry -> entry.renderQueue.stream()).collect(Collectors.toSet());
            String[] tooltipLines = jobs.stream().map(RenderJob::getName).toArray(String[]::new);
            loadButton.setI18nLabel("scenecraft.gui.viewer.bulkrender", jobs.size());
            loadButton.setTooltip(new GuiTooltip().setText(tooltipLines));
            loadButton.setEnabled(!jobs.isEmpty());

            String[] compatError = VideoRenderer.checkCompat(jobs.stream().map(RenderJob::getSettings));
            if (compatError != null) {
                loadButton.setDisabled().setTooltip(new GuiTooltip().setText(compatError));
            }
        } else {
            loadButton.setI18nLabel("scenecraft.gui.load");
            loadButton.setTooltip(null);
            loadButton.setEnabled(count == 1 && !selected.get(0).incompatible);
        }
    }

    private static final GuiImage DEFAULT_THUMBNAIL = new GuiImage().setTexture(Utils.DEFAULT_THUMBNAIL);

    public static class GuiSelectReplayPopup extends AbstractGuiPopup<GuiSelectReplayPopup> {
        private final SettableFuture<File> future = SettableFuture.create();

        private final GuiReplayList list = new GuiReplayList(popup);

        private final GuiButton acceptButton = new GuiButton(popup).setI18nLabel("gui.done").setSize(50, 20).setDisabled();

        private final GuiButton cancelButton = new GuiButton(popup).setI18nLabel("gui.cancel").setSize(50, 20);

        public GuiSelectReplayPopup(GuiContainer container, File folder) {
            super(container);

            list.setFolder(folder);

            list.onSelectionChanged(() -> {
                acceptButton.setEnabled(list.getSelected() != null);
            }).onSelectionDoubleClicked(() -> {
                close();
                future.set(list.getSelected().get(0).file);
            });
            acceptButton.onClick(() -> {
                future.set(list.getSelected().get(0).file);
                close();
            });
            cancelButton.onClick(() -> {
                future.set(null);
                close();
            });

            popup.setLayout(new CustomLayout<GuiPanel>() {
                @Override
                protected void layout(GuiPanel container, int width, int height) {
                    pos(cancelButton, width - width(cancelButton), height - height(cancelButton));
                    pos(acceptButton, x(cancelButton) - 5 - width(acceptButton), y(cancelButton));
                    pos(list, 0, 5);
                    size(list, width, height - height(cancelButton) - 10);
                }

                @Override
                public ReadableDimension calcMinSize(GuiContainer container) {
                    return new Dimension(330, 200);
                }
            });
        }

        public SettableFuture<File> getFuture() {
            return future;
        }
        public GuiButton getAcceptButton() {
            return acceptButton;
        }

        public GuiButton getCancelButton() {
            return cancelButton;
        }

        @Override
        protected GuiSelectReplayPopup getThis() {
            return this;
        }
    }

    public static class GuiReplayList extends AbstractGuiResourceLoadingList<GuiReplayList, GuiReplayEntry> implements KeyHandler, CharHandler {
        private File folder = null;

        // Not actually a child of this element, we just use it for text manipulation
        private final GuiTextField filterTextField = new GuiTextField()
                .setFocused(true);

        public GuiReplayList(GuiContainer container) {
            super(container);
        }

        {
            onLoad((Consumer<Supplier<GuiReplayEntry>> results) -> {
                File[] files = folder.listFiles((FileFilter) new SuffixFileFilter(".mcpr", IOCase.INSENSITIVE));
                if (files == null) {
                    LOGGER.warn("Failed to list files in {}", folder);
                    return;
                }
                Map<File, Long> lastModified = new HashMap<>();
                Arrays.sort(files, Comparator.<File>comparingLong(f -> lastModified.computeIfAbsent(f, File::lastModified)).reversed());
                for (final File file : files) {
                    if (Thread.interrupted()) break;
                    try (ReplayFile replayFile = SceneCraft.instance.files.open(file.toPath())) {
                        final Image thumb = replayFile.getThumbBytes().flatMap(stream -> {
                            try (InputStream in = stream) {
                                return Optional.of(Image.read(in));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return Optional.empty();
                            }
                        }).orElse(null);
                        final ReplayMetaData metaData = replayFile.getMetaData();
                        List<RenderJob> renderQueue = RenderJob.readQueue(replayFile);

                        if (metaData != null) {
                            results.consume(() -> new GuiReplayEntry(file, metaData, thumb, renderQueue) {
                                @Override
                                public ReadableDimension calcMinSize() {
                                    if (isFiltered(this)) {
                                        return new Dimension(-4, -4);
                                    }
                                    return super.calcMinSize();
                                }
                            });
                        }
                    } catch (Exception e) {
                        LOGGER.error("Could not load Replay File {}", file.getName(), e);
                    }
                }
            }).setDrawShadow(true).setDrawSlider(true);
        }

        public void setFolder(File folder) {
            this.folder = folder;
        }

        private boolean isFiltered(GuiReplayEntry entry) {
            String filter = filterTextField.getText().toLowerCase();
            if (filter.isEmpty()) {
                return false;
            }
            return !entry.name.getText().toLowerCase().contains(filter);
        }

        @Override
        public boolean handleKey(KeyInput keyInput) {
            if (keyInput.key == Keyboard.KEY_F1) {
                SettingsRegistry reg = SceneCraft.instance.getSettingsRegistry();
                reg.set(Setting.SHOW_SERVER_IPS, !reg.get(Setting.SHOW_SERVER_IPS));
                reg.save();
                load();
            }

            boolean filterHasPriority = !filterTextField.getText().isEmpty();
            if (filterHasPriority && filterTextField.handleKey(keyInput)) {
                scrollY(0); // ensure we scroll to top if most entries are filtered
                return true;
            }

            if (super.handleKey(keyInput)) {
                return true;
            }

            if (!filterHasPriority && filterTextField.handleKey(keyInput)) {
                scrollY(0); // ensure we scroll to top if most entries are filtered
                return true;
            }

            return false;
        }

        @Override
        public boolean handleChar(CharInput charInput) {
            if (filterTextField.handleChar(charInput)) {
                scrollY(0); // ensure we scroll to top if most entries are filtered
                return true;
            }

            return false;
        }

        @Override
        public void draw(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo) {
            super.draw(renderer, size, renderInfo);

            String filter = filterTextField.getText();
            if (!filter.isEmpty()) {
                boolean anyMatches = getListPanel().calcMinSize().getHeight() > 0;

                Font fontRenderer = getFontRenderer();
                int filterTextWidth = fontRenderer.width(filter);
                int filterTextHeight = fontRenderer.lineHeight;
                renderer.drawRect(
                        size.getWidth() - 3 - 2 - filterTextWidth - 2,
                        size.getHeight() - 3 - 2 - filterTextHeight - 2,
                        2 + filterTextWidth + 2,
                        2 + filterTextHeight + 2,
                        Colors.WHITE
                );
                renderer.drawString(
                        size.getWidth() - 3 - 2 - filterTextWidth,
                        size.getHeight() - 3 - 2 - filterTextHeight,
                        anyMatches ? Colors.BLACK : Colors.DARK_RED,
                        filter
                );
            }
        }

        @Override
        protected GuiReplayList getThis() {
            return this;
        }
    }

    public static class GuiReplayEntry extends AbstractGuiContainer<GuiReplayEntry> implements Comparable<GuiReplayEntry> {
        public final File file;
        public final GuiLabel name = new GuiLabel();
        public final GuiLabel server = new GuiLabel().setColor(Colors.LIGHT_GRAY);
        public final GuiLabel date = new GuiLabel().setColor(Colors.LIGHT_GRAY);
        public final GuiPanel infoPanel = new GuiPanel(this).setLayout(new VerticalLayout().setSpacing(2))
                .addElements(null, name, server, date);
        public final GuiLabel version = new GuiLabel(this).setColor(Colors.RED);
        public final GuiImage thumbnail;
        public final GuiLabel duration = new GuiLabel();
        public final GuiPanel durationPanel = new GuiPanel().setBackgroundColor(Colors.HALF_TRANSPARENT)
                .addElements(null, duration).setLayout(new CustomLayout<GuiPanel>() {
                    @Override
                    protected void layout(GuiPanel container, int width, int height) {
                        pos(duration, 2, 2);
                    }

                    @Override
                    public ReadableDimension calcMinSize(GuiContainer<?> container) {
                        ReadableDimension dimension = duration.calcMinSize();
                        return new Dimension(dimension.getWidth() + 2, dimension.getHeight() + 2);
                    }
                });
        public final GuiImage renderQueueIcon = new GuiImage()
                .setSize(10, 10)
                .setTexture(SceneCraft.TEXTURE, 40, 0, 20, 20);

        private final long dateMillis;
        private final boolean incompatible;
        private final List<RenderJob> renderQueue;

        public GuiReplayEntry(File file, ReplayMetaData metaData, Image thumbImage, List<RenderJob> renderQueue) {
            this.file = file;
            this.renderQueue = renderQueue;

            name.setText(ChatFormatting.UNDERLINE + Utils.fileNameToReplayName(file.getName()));
            if (!StringUtils.isEmpty(metaData.getCustomServerName())) {
                server.setText(metaData.getCustomServerName());
            } else if (StringUtils.isEmpty(metaData.getServerName())
                    || !SceneCraft.instance.getSettingsRegistry().get(Setting.SHOW_SERVER_IPS)) {
                server.setI18nText("scenecraft.gui.iphidden").setColor(Colors.DARK_RED);
            } else {
                server.setText(metaData.getServerName());
            }
            incompatible = !SceneCraft.isCompatible(metaData.getFileFormatVersion(), metaData.getRawProtocolVersionOr0());
            if (incompatible) {
                version.setText("Minecraft " + metaData.getMcVersion());
            }
            dateMillis = metaData.getDate();
            date.setText(new SimpleDateFormat().format(new Date(dateMillis)));
            if (thumbImage == null) {
                thumbnail = new GuiImage(DEFAULT_THUMBNAIL).setSize(30 * 16 / 9, 30);
                addElements(null, thumbnail);
            } else {
                thumbnail = new GuiImage(this).setTexture(thumbImage).setSize(30 * 16 / 9, 30);
            }
            duration.setText(Utils.convertSecondsToShortString(metaData.getDuration() / 1000));
            addElements(null, durationPanel);

            if (!renderQueue.isEmpty()) {
                renderQueueIcon.setTooltip(new GuiTooltip()
                        .setText(renderQueue.stream().map(RenderJob::getName).toArray(String[]::new)));
                addElements(null, renderQueueIcon);
            }

            setLayout(new CustomLayout<GuiReplayEntry>() {
                @Override
                protected void layout(GuiReplayEntry container, int width, int height) {
                    pos(thumbnail, 0, 0);
                    x(durationPanel, width(thumbnail) - width(durationPanel));
                    y(durationPanel, height(thumbnail) - height(durationPanel));

                    pos(infoPanel, width(thumbnail) + 5, 0);
                    pos(version, width - width(version), 0);

                    if (renderQueueIcon.getContainer() != null) {
                        pos(renderQueueIcon, width(thumbnail) - width(renderQueueIcon), 0);
                    }
                }

                @Override
                public ReadableDimension calcMinSize(GuiContainer<?> container) {
                    return new Dimension(300, thumbnail.getMinSize().getHeight());
                }
            });
        }

        @Override
        protected GuiReplayEntry getThis() {
            return this;
        }

        @Override
        public int compareTo(GuiReplayEntry o) {
            return Long.compare(o.dateMillis, dateMillis);
        }
    }
}
