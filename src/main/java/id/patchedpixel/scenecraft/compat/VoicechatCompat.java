package id.patchedpixel.scenecraft.compat;

import de.maxhenkel.voicechat.VoicechatClient;

/**
 * Isolated from {@link SceneCraftCompat} so that Simple Voice Chat classes are only loaded when the mod is present.
 * Never call this class unless {@code ModList.get().isLoaded("voicechat")}.
 */
final class VoicechatCompat {
    private VoicechatCompat() {}

    /** @return the previous value of hideIcons */
    static boolean isHidden() {
        return VoicechatClient.CLIENT_CONFIG.hideIcons.get();
    }

    static void setHidden(boolean hidden) {
        VoicechatClient.CLIENT_CONFIG.hideIcons.set(hidden);
    }
}
