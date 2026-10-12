package id.patchedpixel.scenecraft.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static id.patchedpixel.scenecraft.core.versions.MCVer.*;

import net.minecraft.client.Camera;

@Mixin(value = Camera.class)
public abstract class Mixin_StabilizeCamera {
    @WrapOperation(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void sceneCraftRender_stabilizeCamera(Camera instance, float yRot, float xRot, Operation<Void> original) {
        if (getHandler() != null) {
            RenderSettings settings = getHandler().getSettings();

            if (settings.isStabilizeYaw()) {
                yRot = 0f;
            }

            if (settings.isStabilizePitch()) {
                xRot = 0f;
            }

            // Roll handled in MixinCamera
        }

        original.call(instance, yRot, xRot);
    }

    @Unique
    private EntityRendererHandler getHandler() {
        return ((EntityRendererHandler.IEntityRenderer) getMinecraft().gameRenderer).sceneCraftRender_getHandler();
    }
}
