package id.patchedpixel.scenecraft.mixin.record;

import id.patchedpixel.scenecraft.record.SceneCraftRecording;
import id.patchedpixel.scenecraft.record.handler.RecordingEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.core.BlockPos;

@Mixin(ClientLevel.class)
public abstract class MixinRenderGlobal {
    @Inject(method = "destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V", at = @At("HEAD"))
    public void saveBlockBreakProgressPacket(int breakerId,
                                             BlockPos pos,
                                             int progress, CallbackInfo info) {
        RecordingEventHandler recordingEventHandler = SceneCraftRecording.instance.getConnectionEventHandler().getRecordingEventHandler();
        if (recordingEventHandler != null) {
            recordingEventHandler.onBlockBreakAnim(breakerId,
                    pos,
                    progress);
        }
    }
}
