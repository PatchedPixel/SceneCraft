package id.patchedpixel.scenecraft.render.processor;

import id.patchedpixel.scenecraft.render.frame.BitmapFrame;
import id.patchedpixel.scenecraft.render.frame.StereoscopicOpenGlFrame;
import id.patchedpixel.scenecraft.render.utils.ByteBufferPool;
import id.patchedpixel.scenecraft.gui.utils.geometry.Dimension;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;

import java.nio.ByteBuffer;

import static id.patchedpixel.scenecraft.render.utils.Utils.openGlBytesToBitmap;

public class StereoscopicToBitmapProcessor extends AbstractFrameProcessor<StereoscopicOpenGlFrame, BitmapFrame> {
    @Override
    public BitmapFrame process(StereoscopicOpenGlFrame rawFrame) {
        ReadableDimension size = rawFrame.getLeft().getSize();
        int width = size.getWidth();
        int bpp = rawFrame.getLeft().getBytesPerPixel();
        ByteBuffer result = ByteBufferPool.allocate(width * 2 * size.getHeight() * bpp);
        openGlBytesToBitmap(rawFrame.getLeft(), 0, 0, result, width * 2);
        openGlBytesToBitmap(rawFrame.getRight(), size.getWidth(), 0, result, width * 2);
        ByteBufferPool.release(rawFrame.getLeft().getByteBuffer());
        ByteBufferPool.release(rawFrame.getRight().getByteBuffer());
        return new BitmapFrame(rawFrame.getFrameId(), new Dimension(width * 2, size.getHeight()), bpp, result);
    }
}
