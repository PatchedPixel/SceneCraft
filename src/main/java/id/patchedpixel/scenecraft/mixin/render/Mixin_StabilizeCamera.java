package id.patchedpixel.scenecraft.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import id.patchedpixel.scenecraft.render.RenderSettings;
import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.replay.camera.CameraEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static id.patchedpixel.scenecraft.core.versions.MCVer.*;

import net.minecraft.client.Camera;

@Mixin(value = Camera.class)
public abstract class Mixin_StabilizeCamera {
    @WrapOperation(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void replayModRender_stabilizeCamera(Camera instance, float yRot, float xRot, Operation<Void> original) {
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
        return ((EntityRendererHandler.IEntityRenderer) getMinecraft().gameRenderer).replayModRender_getHandler();
    }
}
