package id.patchedpixel.scenecraft.mixin.core;

import id.patchedpixel.scenecraft.core.versions.MCVer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.PacketProcessor;
import org.spongepowered.asm.mixin.Final;
import id.patchedpixel.scenecraft.core.events.PostRenderCallback;
import id.patchedpixel.scenecraft.core.events.PreRenderCallback;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft extends ReentrantBlockableEventLoop<Runnable> implements MCVer.MinecraftMethodAccessor {
    @Shadow protected abstract void handleKeybinds();

    @Override
    public void replayModProcessKeyBinds() {
        handleKeybinds();
    }

    @Shadow @Final private com.mojang.blaze3d.platform.SDLEventHandler sdlEventHandler;

    @Override
    public void replayModPollEvents() {
        com.mojang.blaze3d.systems.RenderSystem.pollEvents(this.sdlEventHandler);
    }

    @Override
    public void replayModPumpEvents() {
        com.mojang.blaze3d.systems.RenderSystem.pumpEvents(this.sdlEventHandler);
    }

    @Shadow @Final private PacketProcessor packetProcessor;
    @Override
    public void replayModExecuteTaskQueue() {
        this.packetProcessor.processQueuedPackets();
        runAllTasks();
    }

    private static final String GAME_RENDERER_RENDER = "Lnet/minecraft/client/Minecraft;renderFrame(Z)V";

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = GAME_RENDERER_RENDER))
    private void preRender(boolean unused, CallbackInfo ci) {
        PreRenderCallback.EVENT.invoker().preRender();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = GAME_RENDERER_RENDER, shift = At.Shift.AFTER))
    private void postRender(boolean unused, CallbackInfo ci) {
        PostRenderCallback.EVENT.invoker().postRender();
    }

    MixinMinecraft() { super(null, false); }
}
