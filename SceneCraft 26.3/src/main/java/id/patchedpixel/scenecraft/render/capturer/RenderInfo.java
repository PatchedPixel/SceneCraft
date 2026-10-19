package id.patchedpixel.scenecraft.render.capturer;

import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;

public interface RenderInfo {
    ReadableDimension getFrameSize();

    int getFramesDone();

    int getTotalFrames();

    float updateForNextFrame();

    RenderSettings getRenderSettings();
}
