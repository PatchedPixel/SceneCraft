package id.patchedpixel.scenecraft.mixin.core;

import id.patchedpixel.scenecraft.core.events.PostRenderWorldCallback;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelRenderer.class)
public class Mixin_PostRenderWorldCalback {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;"))
    private void postRenderWorld(CallbackInfo ci) {
        PoseStack matrixStack = new PoseStack();
        PostRenderWorldCallback.EVENT.invoker().postRenderWorld(matrixStack, this.submitNodeStorage);
    }

    @Shadow @Final private SubmitNodeStorage submitNodeStorage;
}
