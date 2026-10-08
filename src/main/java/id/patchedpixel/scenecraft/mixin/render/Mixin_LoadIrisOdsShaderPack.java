package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.capturer.IrisODSFrameCapturer;
import net.irisshaders.iris.Iris;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;

@Pseudo
@Mixin(value = Iris.class, remap = false)
public class Mixin_LoadIrisOdsShaderPack {
    @Redirect(method = "loadExternalShaderpack", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/Iris;getShaderpacksDirectory()Ljava/nio/file/Path;"))
    private static Path loadReplayModOdsPack(String name) {
        if (IrisODSFrameCapturer.INSTANCE != null && IrisODSFrameCapturer.SHADER_PACK_NAME.equals(name)) {
            // Root of our own mod file (contains the shaders/ directory of the ODS shader pack)
            return ModList.get().getModFileById("scenecraft").getFile().findResource("assets").getParent();
        } else {
            return Iris.getShaderpacksDirectory();
        }
    }
}
