package id.patchedpixel.scenecraft.render.capturer;

import id.patchedpixel.scenecraft.render.frame.CubicOpenGlFrame;
import id.patchedpixel.scenecraft.render.rendering.Channel;

import java.util.Collections;
import java.util.Map;

public class CubicOpenGlFrameCapturer extends OpenGlFrameCapturer<CubicOpenGlFrame, CubicOpenGlFrameCapturer.Data> {
    public enum Data implements CaptureData {
        LEFT, RIGHT, FRONT, BACK, TOP, BOTTOM
    }

    private final int frameSize;
    public CubicOpenGlFrameCapturer(WorldRenderer worldRenderer, RenderInfo renderInfo, int frameSize) {
        super(worldRenderer, renderInfo);
        this.frameSize = frameSize;
        worldRenderer.setOmnidirectional(true);
    }

    @Override
    protected int getFrameWidth() {
        return frameSize;
    }

    @Override
    protected int getFrameHeight() {
        return frameSize;
    }

    @Override
    public Map<Channel, CubicOpenGlFrame> process() {
        renderInfo.updateForNextFrame();
        int frameId = framesDone++;
        CubicOpenGlFrame frame =  new CubicOpenGlFrame(renderFrame(frameId, Data.LEFT),
                renderFrame(frameId, Data.RIGHT),
                renderFrame(frameId, Data.FRONT),
                renderFrame(frameId, Data.BACK),
                renderFrame(frameId, Data.TOP),
                renderFrame(frameId, Data.BOTTOM));
        return Collections.singletonMap(Channel.BRGA, frame);
    }
}
