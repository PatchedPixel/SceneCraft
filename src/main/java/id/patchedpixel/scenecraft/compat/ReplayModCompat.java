package id.patchedpixel.scenecraft.compat;

import id.patchedpixel.scenecraft.compat.optifine.DisableFastRender;
import id.patchedpixel.scenecraft.core.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import id.patchedpixel.scenecraft.compat.shaders.ShaderBeginRender;

public class ReplayModCompat implements Module {
    public static Logger LOGGER = LogManager.getLogger();

    @Override
    public void initClient() {
        new ShaderBeginRender().register();
        new DisableFastRender().register();
    }
}
