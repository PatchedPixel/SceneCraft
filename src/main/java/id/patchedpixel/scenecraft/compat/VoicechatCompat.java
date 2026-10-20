package id.patchedpixel.scenecraft.compat;

import de.maxhenkel.voicechat.VoicechatClient;

/**
 * Isolated from {@link SceneCraftCompat} so that Simple Voice Chat classes are only loaded when the mod is present.
 * Never call this class unless {@code ModList.get().isLoaded("voicechat")}.
 */
final class VoicechatCompat {
    private VoicechatCompat() {}

    private static boolean userSetting;

    static boolean isHidden() {
        return userSetting;
    }

    static void setHidden(boolean hidden) {
        VoicechatClient.CLIENT_CONFIG.hideIcons.set(hidden);
    }
}
