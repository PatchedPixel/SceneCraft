package id.patchedpixel.scenecraft.compat;

import id.patchedpixel.scenecraft.compat.optifine.DisableFastRender;
import id.patchedpixel.scenecraft.compat.shaders.ShaderBeginRender;
import id.patchedpixel.scenecraft.core.Module;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hides other mods' HUD elements (Simple Voice Chat icons, Xaero's Minimap) while a replay is active.
 * <p>
 * Voice chat has no runtime toggle, only a persisted config option, so hiding it modifies the user's config file.
 * To make sure this is always undone even if the game crashes or the JVM is killed mid-replay:
 * <ul>
 *   <li>a marker file is written before the option is changed and removed after it is restored,</li>
 *   <li>a JVM shutdown hook restores it on normal exit, crash and force close (window close, System.exit),</li>
 *   <li>if a marker is found on the next start (hard kill / power loss), the option is restored then.</li>
 * </ul>
 * The original value is remembered, so a user who had icons hidden on purpose keeps them hidden.
 */
public class SceneCraftCompat implements Module {
    public static Logger LOGGER = LogManager.getLogger();

    private static final String MARKER_NAME = "scenecraft-voicechat-hud.marker";

    private static volatile boolean minimapHide;
    /** True while we have changed the voice chat option and have not restored it yet. */
    private static boolean voicechatChanged;
    private static boolean hooked;

    public static boolean shouldHide() {
        return minimapHide;
    }

    public static synchronized void hideModHud() {
        minimapHide = true;
        if (!isLoaded("voicechat") || voicechatChanged) {
            return;
        }
        try {
            if (VoicechatCompat.isHidden()) {
                return; // user already hides them, nothing to change or restore
            }
            installHook();
            writeMarker();
            voicechatChanged = true;
            VoicechatCompat.setHidden(true);
        } catch (Throwable t) {
            LOGGER.warn("Could not hide Simple Voice Chat icons", t);
        }
    }

    public static synchronized void showModHud() {
        minimapHide = false;
        if (!voicechatChanged) {
            return;
        }
        try {
            VoicechatCompat.setHidden(false);
            voicechatChanged = false;
            deleteMarker();
        } catch (Throwable t) {
            LOGGER.warn("Could not restore Simple Voice Chat icons", t);
        }
    }

    /**
     * Restores the voice chat option if a previous session ended without restoring it.
     * Safe to call repeatedly; does nothing in the common case.
     */
    public static synchronized void recoverStaleHud() {
        if (voicechatChanged || !isLoaded("voicechat")) {
            return;
        }
        try {
            if (Files.exists(markerPath())) {
                VoicechatCompat.setHidden(false);
                deleteMarker();
                LOGGER.info("Restored Simple Voice Chat icons left hidden by an earlier replay session");
            }
        } catch (Throwable t) {
            LOGGER.debug("Voice chat not ready for HUD recovery yet", t);
        }
    }

    private static boolean isLoaded(String modId) {
        try {
            return ModList.get() != null && ModList.get().isLoaded(modId);
        } catch (Throwable t) {
            return false;
        }
    }

    private static void installHook() {
        if (hooked) return;
        hooked = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                showModHud();
            } catch (Throwable ignored) {
            }
        }, "scenecraft-hud-restore"));
    }

    private static Path markerPath() {
        return FMLPaths.CONFIGDIR.get().resolve(MARKER_NAME);
    }

    private static void writeMarker() {
        try {
            Files.writeString(markerPath(), "SceneCraft hid the Simple Voice Chat icons; they will be restored automatically.\n");
        } catch (Exception e) {
            LOGGER.warn("Could not write HUD marker", e);
        }
    }

    private static void deleteMarker() {
        try {
            Files.deleteIfExists(markerPath());
        } catch (Exception e) {
            LOGGER.warn("Could not delete HUD marker", e);
        }
    }

    @Override
    public void initClient() {
        new ShaderBeginRender().register();
        new DisableFastRender().register();
        recoverStaleHud();
    }
}
