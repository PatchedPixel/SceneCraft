package id.patchedpixel.scenecraft.render.rendering;

import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.render.capturer.CubicPboOpenGlFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.RenderInfo;
import id.patchedpixel.scenecraft.render.capturer.SimplePboOpenGlFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.StereoscopicPboOpenGlFrameCapturer;
import id.patchedpixel.scenecraft.render.capturer.WorldRenderer;
import id.patchedpixel.scenecraft.render.frame.CubicOpenGlFrame;
import id.patchedpixel.scenecraft.render.frame.ODSOpenGlFrame;
import id.patchedpixel.scenecraft.render.frame.OpenGlFrame;
import id.patchedpixel.scenecraft.render.frame.BitmapFrame;
import id.patchedpixel.scenecraft.render.frame.StereoscopicOpenGlFrame;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.render.processor.CubicToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.EquirectangularToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.ODSToBitmapProcessor;
import id.patchedpixel.scenecraft.render.processor.OpenGlToBitmapProcessor;
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

    public static Pipeline<OpenGlFrame, BitmapFrame> newDefaultPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<OpenGlFrame> capturer;
        capturer = new SimplePboOpenGlFrameCapturer(worldRenderer, renderInfo);
        return new Pipeline<>(worldRenderer, capturer, new OpenGlToBitmapProcessor(), consumer);
    }

    public static Pipeline<StereoscopicOpenGlFrame, BitmapFrame> newStereoscopicPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<StereoscopicOpenGlFrame> capturer;
        capturer = new StereoscopicPboOpenGlFrameCapturer(worldRenderer, renderInfo);
        return new Pipeline<>(worldRenderer, capturer, new StereoscopicToBitmapProcessor(), consumer);
    }

    public static Pipeline<CubicOpenGlFrame, BitmapFrame> newCubicPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);
        FrameCapturer<CubicOpenGlFrame> capturer;
        capturer = new CubicPboOpenGlFrameCapturer(worldRenderer, renderInfo, settings.getVideoWidth() / 4);
        return new Pipeline<>(worldRenderer, capturer, new CubicToBitmapProcessor(), consumer);
    }

    public static Pipeline<CubicOpenGlFrame, BitmapFrame> newEquirectangularPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);

        EquirectangularToBitmapProcessor processor = new EquirectangularToBitmapProcessor(settings.getVideoWidth(),
                settings.getVideoHeight(), settings.getSphericalFovX());

        FrameCapturer<CubicOpenGlFrame> capturer;
        capturer = new CubicPboOpenGlFrameCapturer(worldRenderer, renderInfo, processor.getFrameSize());
        return new Pipeline<>(worldRenderer, capturer, processor, consumer);
    }

    public static Pipeline<ODSOpenGlFrame, BitmapFrame> newODSPipeline(RenderInfo renderInfo, FrameConsumer<BitmapFrame> consumer) {
        RenderSettings settings = renderInfo.getRenderSettings();
        WorldRenderer worldRenderer = new EntityRendererHandler(settings, renderInfo);

        ODSToBitmapProcessor processor = new ODSToBitmapProcessor(settings.getVideoWidth(),
                settings.getVideoHeight(), settings.getSphericalFovX());

        if (!RenderSettings.RenderMethod.ODS.isSupported()) {
            throw new UnsupportedOperationException("ODS rendering requires Iris on the OpenGL backend");
        }
        FrameCapturer<ODSOpenGlFrame> capturer =
                new id.patchedpixel.scenecraft.render.capturer.IrisODSFrameCapturer(worldRenderer, renderInfo, processor.getFrameSize());
        return new Pipeline<>(worldRenderer, capturer, processor, consumer);
    }
}
