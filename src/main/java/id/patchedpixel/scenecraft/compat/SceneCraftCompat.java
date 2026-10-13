package id.patchedpixel.scenecraft.compat;

import de.maxhenkel.voicechat.VoicechatClient;
import id.patchedpixel.scenecraft.compat.optifine.DisableFastRender;
import id.patchedpixel.scenecraft.core.Module;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import id.patchedpixel.scenecraft.compat.shaders.ShaderBeginRender;

public class SceneCraftCompat implements Module {
    public static Logger LOGGER = LogManager.getLogger();

    private static boolean minimapHide;

    public static boolean shouldHide() {
        return minimapHide;
    }

    public static void hideModHud() {
        if(ModList.get().isLoaded("voicechat")) {
            VoicechatClient.CLIENT_CONFIG.hideIcons.set(true);
        }
        if(ModList.get().isLoaded("xaerominimap")) {
            minimapHide = true;
        }
    }

    public static void showModHud() {
        if(ModList.get().isLoaded("voicechat")) {
            VoicechatClient.CLIENT_CONFIG.hideIcons.set(false);
        }
        if(ModList.get().isLoaded("xaerominimap")) {
            minimapHide = false;
        }
    }

    @Override
    public void initClient() {
        new ShaderBeginRender().register();
        new DisableFastRender().register();
    }
}
