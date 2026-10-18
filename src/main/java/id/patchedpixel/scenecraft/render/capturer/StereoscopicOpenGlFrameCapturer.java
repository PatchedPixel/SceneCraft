package id.patchedpixel.scenecraft.render.capturer;

import id.patchedpixel.scenecraft.render.frame.OpenGlFrame;
import id.patchedpixel.scenecraft.render.frame.StereoscopicOpenGlFrame;
import id.patchedpixel.scenecraft.render.rendering.Channel;

import java.util.Collections;
import java.util.Map;

public class StereoscopicOpenGlFrameCapturer extends OpenGlFrameCapturer<StereoscopicOpenGlFrame, StereoscopicOpenGlFrameCapturer.Data> {
    public enum Data implements CaptureData {
        LEFT_EYE, RIGHT_EYE
    }

    public StereoscopicOpenGlFrameCapturer(WorldRenderer worldRenderer, RenderInfo renderInfo) {
        super(worldRenderer, renderInfo);
    }

    @Override
    protected int getFrameWidth() {
        return super.getFrameWidth() / 2;
    }

    @Override
    public Map<Channel, StereoscopicOpenGlFrame> process() {
        int frameId = framesDone++;
        OpenGlFrame left = renderFrame(frameId, Data.LEFT_EYE);
        OpenGlFrame right = renderFrame(frameId, Data.RIGHT_EYE);
        StereoscopicOpenGlFrame frame = new StereoscopicOpenGlFrame(left, right);
        return Collections.singletonMap(Channel.BRGA, frame);
    }
}
