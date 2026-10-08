package id.patchedpixel.scenecraft.render.processor;

import id.patchedpixel.scenecraft.render.rendering.Frame;

public class DummyProcessor<F extends Frame> extends AbstractFrameProcessor<F, F> {
    @Override
    public F process(F rawFrame) {
        return rawFrame;
    }
}
