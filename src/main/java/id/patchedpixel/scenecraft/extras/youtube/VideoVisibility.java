package id.patchedpixel.scenecraft.extras.youtube;

import net.minecraft.client.resources.language.I18n;

// The YouTube uploader is not working anymore
@Deprecated(forRemoval = true)
public enum VideoVisibility {
    PUBLIC, UNLISTED, PRIVATE;

    @Override
    public String toString() {
        return I18n.get("scenecraft.gui.videovisibility." + name().toLowerCase());
    }
}
