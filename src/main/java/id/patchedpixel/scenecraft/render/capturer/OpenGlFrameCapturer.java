package id.patchedpixel.scenecraft.render.capturer;

import com.mojang.blaze3d.systems.RenderSystem;
import id.patchedpixel.scenecraft.core.versions.MCVer;
import id.patchedpixel.scenecraft.render.frame.OpenGlFrame;
import id.patchedpixel.scenecraft.render.rendering.Frame;
import id.patchedpixel.scenecraft.render.rendering.FrameCapturer;
import id.patchedpixel.scenecraft.render.utils.ByteBufferPool;
import id.patchedpixel.scenecraft.gui.utils.geometry.Dimension;
import id.patchedpixel.scenecraft.gui.utils.geometry.ReadableDimension;
import id.patchedpixel.scenecraft.gui.utils.geometry.WritableDimension;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;

import java.io.IOException;
import java.nio.ByteBuffer;

import static id.patchedpixel.scenecraft.core.versions.MCVer.popMatrix;
import static id.patchedpixel.scenecraft.core.versions.MCVer.pushMatrix;
import static id.patchedpixel.scenecraft.core.versions.MCVer.resizeMainWindow;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.device.GpuDevice;

public abstract class OpenGlFrameCapturer<F extends Frame, D extends CaptureData> implements FrameCapturer<F> {
    protected final WorldRenderer worldRenderer;
    protected final RenderInfo renderInfo;
    protected int framesDone;
    private RenderTarget frameBuffer;

    protected final Minecraft mc = MCVer.getMinecraft();

    public OpenGlFrameCapturer(WorldRenderer worldRenderer, RenderInfo renderInfo) {
        this.worldRenderer = worldRenderer;
        this.renderInfo = renderInfo;
    }

    protected final ReadableDimension frameSize = new ReadableDimension() {
        @Override
        public int getWidth() {
            return getFrameWidth();
        }

        @Override
        public int getHeight() {
            return getFrameHeight();
        }

        @Override
        public void getSize(WritableDimension dest) {
            dest.setSize(getWidth(), getHeight());
        }
    };

    protected int getFrameWidth() {
        return renderInfo.getFrameSize().getWidth();
    }

    protected int getFrameHeight() {
        return renderInfo.getFrameSize().getHeight();
    }

    protected RenderTarget frameBuffer() {
        if (frameBuffer == null) {
            frameBuffer = mc.gameRenderer.mainRenderTarget();
        }
        return frameBuffer;
    }

    @Override
    public boolean isDone() {
        return framesDone >= renderInfo.getTotalFrames();
    }

    protected OpenGlFrame renderFrame(int frameId, float partialTicks) {
        return renderFrame(frameId, partialTicks, null);
    }

    protected OpenGlFrame renderFrame(int frameId, float partialTicks, D captureData) {
        resizeMainWindow(mc, getFrameWidth(), getFrameHeight());

        pushMatrix();

        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(mc.gameRenderer.mainRenderTarget().getColorTexture(), new org.joml.Vector4f(), mc.gameRenderer.mainRenderTarget().getDepthTexture(), 0);

        worldRenderer.renderWorld(partialTicks, captureData);

        popMatrix();

        RenderSystem.getDynamicUniforms().reset();
        mc.levelRenderer.endFrame();
        RenderSystem.getDevice().createCommandEncoder().submit();

        return captureFrame(frameId, captureData);
    }

    protected OpenGlFrame captureFrame(int frameId, D captureData) {
        ByteBuffer buffer = ByteBufferPool.allocate(getFrameWidth() * getFrameHeight() * 4);
        GpuDevice device = RenderSystem.getDevice();
        try (GpuBuffer gpuBuffer = device.createBuffer(null, GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_READ, getFrameWidth() * getFrameHeight() * 4)) {
            device.createCommandEncoder().copyTextureToBuffer(frameBuffer().getColorTexture(), gpuBuffer, 0, () -> {}, 0);
            try (GpuBufferSlice.MappedView view = gpuBuffer.map(true, false)) {
                buffer.put(view.data());
            }
        }
        buffer.rewind();

        return new OpenGlFrame(frameId, new Dimension(getFrameWidth(), getFrameHeight()), 4, buffer);
    }

    @Override
    public void close() throws IOException {
    }
}
