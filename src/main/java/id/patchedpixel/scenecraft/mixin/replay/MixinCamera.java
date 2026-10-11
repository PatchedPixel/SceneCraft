package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import id.patchedpixel.scenecraft.replay.camera.CameraEntity;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static id.patchedpixel.scenecraft.core.versions.MCVer.getMinecraft;

/**
 * Applies the replay camera roll.
 * <p>
 * Instead of modifying the Z argument of {@code Quaternionf.rotationYXZ} inside {@code setRotation}
 * (fragile: depends on the exact call in the method body), the roll is appended to the finished rotation:
 * {@code rotationYXZ(y, x, z)} equals {@code Ry * Rx * Rz}, so adding to z is the same as post-multiplying {@code rotateZ}.
 */
@Mixin(Camera.class)
public class MixinCamera {
    @Shadow private @Nullable Entity entity;
    @Shadow @Final private static Vector3fc FORWARDS;
    @Shadow @Final private static Vector3fc UP;
    @Shadow @Final private static Vector3fc LEFT;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;
    @Shadow private int matrixPropertiesDirty;

    @Inject(method = "setRotation(FFF)V", at = @At("TAIL"))
    private void sceneCraftReplay_applyRoll(float yRot, float xRot, float roll, CallbackInfo ci) {
        Entity entity = this.entity;
        if (!(entity instanceof CameraEntity)) return;

        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) getMinecraft().gameRenderer).sceneCraftRender_getHandler();
        if (handler != null && handler.getSettings().isStabilizeRoll()) return;

        this.rotation.rotateZ(-((CameraEntity) entity).roll * (float) Math.PI / 180f);

        // Same as the tail of Camera.setRotation
        FORWARDS.rotate(this.rotation, this.forwards);
        UP.rotate(this.rotation, this.up);
        LEFT.rotate(this.rotation, this.left);
        this.matrixPropertiesDirty |= 3;
    }
}
