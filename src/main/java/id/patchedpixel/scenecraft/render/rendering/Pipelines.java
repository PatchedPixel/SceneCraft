package id.patchedpixel.scenecraft.render.rendering;

import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.render.capturer.CubicPboGpuFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.RenderInfo;
import id.patchedpixel.scenecraft.render.capturer.SimplePboGpuFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.StereoscopicPboGpuFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.WorldRenderer;
import id.patchedpixel.scenecraft.render.frame.CubicGpuFrame;
import id.patchedpixel.scenecraft.render.frame.ODSGpuFrame;
import id.patchedpixel.scenecraft.render.frame.GpuFrame;
import id.patchedpixel.scenecraft.render.frame.BitmapFrame;
import id.patchedpixel.scenecraft.render.frame.StereoscopicGpuFrame;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.render.processor.CubicToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.EquirectangularToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.ODSToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.GpuToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.StereoscopicToBitmapProcessor;

public class Pipelines {
    public static Pipeline newPipeline(RenderSettings.RenderMethod method, RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        switch (method) {
            case DEFAULT:
                return newDefaultPipeline(renderInfo, consumer);
            case STEREOSCOPIC:
                return newStereoscopicPipeline(renderInfo, consumer);
            case CUBIC:
                return newCubicPipeline(renderInfo, consumer);
            case EQUIRECTANGULAR:
                return newEquirectangularPipeline(renderInfo, consumer);
            case ODS:
                return newODSPipeline(renderInfo, consumer);
        }
        throw new UnsupportedOperationException("Unknown method: " + method);
    }

    public static Pipeline<GpuFrame, BitmapFrame> newDefaultPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<GpuFrame> capturer;
        capturer = new SimplePboGpuFrameCapturer(worldRenderer, renderInfo);
        return new Pipeline<>(worldRenderer, capturer, new GpuToBitmapProcessor(), consumer);
    }

    public static Pipeline<StereoscopicGpuFrame, BitmapFrame> newStereoscopicPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<StereoscopicGpuFrame> capturer;
        capturer = new StereoscopicPboGpuFrameCapturer(worldRenderer, renderInfo);
        return new Pipeline<>(worldRenderer, capturer, new StereoscopicToBitmapProcessor(), consumer);
    }

    public static Pipeline<CubicGpuFrame, BitmapFrame> newCubicPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<CubicGpuFrame> capturer;
        capturer = new CubicPboGpuFrameCapturer(worldRenderer, renderInfo, settings.getVideoWidth() / 4);
        return new Pipeline<>(worldRenderer, capturer, new CubicToBitmapProcessor(), consumer);
    }

    public static Pipeline<CubicGpuFrame, BitmapFrame> newEquirectangularPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);

        EquirectangularToBitmapProcessor processor = new EquirectangularToBitmapProcessor(settings.getVideoWidth(),
                settings.getVideoHeight(), settings.getSphericalFovX());

        FrameCapturer<CubicGpuFrame> capturer;
        capturer = new CubicPboGpuFrameCapturer(worldRenderer, renderInfo, processor.getFrameSize());
        return new Pipeline<>(worldRenderer, capturer, processor, consumer);
    }

    public static Pipeline<ODSGpuFrame, BitmapFrame> newODSPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);

        ODSToBitmapProcessor processor = new ODSToBitmapProcessor(settings.getVideoWidth(),
                settings.getVideoHeight(), settings.getSphericalFovX());

        if (!RenderSettings.RenderMethod.ODS.isSupported()) {
            throw new UnsupportedOperationException("ODS rendering requires Iris on the OpenGL backend");
        }
        FrameCapturer<ODSGpuFrame> capturer =
                new id.patchedpixel.scenecraft.render.capturer.IrisODSFrameCapturer(worldRenderer, renderInfo, processor.getFrameSize());
        return new Pipeline<>(worldRenderer, capturer, processor, consumer);
    }
}
