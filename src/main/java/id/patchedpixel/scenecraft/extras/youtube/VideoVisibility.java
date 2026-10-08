package id.patchedpixel.scenecraft.extras.youtube;

import net.minecraft.client.resources.language.I18n;

public enum VideoVisibility {
    PUBLIC, UNLISTED, PRIVATE;

    @Override
    public String toString() {
        return I18n.get("scenecraft.gui.videovisibility." + name().toLowerCase());
    }
}
