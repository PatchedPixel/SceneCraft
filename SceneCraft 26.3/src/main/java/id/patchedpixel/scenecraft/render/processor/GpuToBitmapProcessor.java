package id.patchedpixel.scenecraft.render.processor;

import id.patchedpixel.scenecraft.render.frame.GpuFrame;
import id.patchedpixel.scenecraft.render.frame.BitmapFrame;
import id.patchedpixel.scenecraft.render.utils.ByteBufferPool;
import id.patchedpixel.scenecraft.gui.utils.geometry.Dimension;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;
import id.patchedpixel.scenecraft.render.utils.Utils;

import java.nio.ByteBuffer;

import static id.patchedpixel.scenecraft.render.utils.Utils.gpuBytesToBitmap;

public class GpuToBitmapProcessor extends AbstractFrameProcessor<GpuFrame, BitmapFrame> {
    @Override
    public BitmapFrame process(GpuFrame rawFrame) {
        ReadableDimension size = rawFrame.getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        int bpp = rawFrame.getBytesPerPixel();
        ByteBuffer result = ByteBufferPool.allocate(width * height * bpp);
        Utils.gpuBytesToBitmap(rawFrame, 0, 0, result, width);
        ByteBufferPool.release(rawFrame.getByteBuffer());
        return new BitmapFrame(rawFrame.getFrameId(), new Dimension(width, height), bpp, result);
    }
}
