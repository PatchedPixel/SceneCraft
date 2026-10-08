package id.patchedpixel.scenecraft.render.frame;

import id.patchedpixel.scenecraft.render.rendering.Frame;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;

import java.nio.ByteBuffer;

public class OpenGlFrame implements Frame {
    private final int frameId;
    private final ReadableDimension size;
    private final int bytesPerPixel;
    private final ByteBuffer byteBuffer;

    public OpenGlFrame(int frameId, ReadableDimension size, int bytesPerPixel, ByteBuffer byteBuffer) {
        this.frameId = frameId;
        this.size = size;
        this.bytesPerPixel = bytesPerPixel;
        this.byteBuffer = byteBuffer;
    }

    public int getFrameId() {
        return this.frameId;
    }

    public ReadableDimension getSize() {
        return this.size;
    }

    public int getBytesPerPixel() {
        return bytesPerPixel;
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }
}
