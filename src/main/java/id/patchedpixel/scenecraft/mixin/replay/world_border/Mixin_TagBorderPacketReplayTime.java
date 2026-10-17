package id.patchedpixel.scenecraft.mixin.replay.world_border;

import id.patchedpixel.scenecraft.replay.BorderReplayTime;
import id.patchedpixel.scenecraft.replay.FullReplaySender;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class Mixin_TagBorderPacketReplayTime {
    @Inject(method = "handleSetBorderLerpSize", at = @At("HEAD"))
    private void scenecraft$lerpStart(ClientboundSetBorderLerpSizePacket packet, CallbackInfo ci) {
        BorderReplayTime.pending = FullReplaySender.borderPacketTime(packet);
    }

    @Inject(method = "handleSetBorderLerpSize", at = @At("RETURN"))
    private void scenecraft$lerpEnd(ClientboundSetBorderLerpSizePacket packet, CallbackInfo ci) {
        BorderReplayTime.pending = -1L;
        FullReplaySender.forgetBorderPacket(packet);
    }

    @Inject(method = "handleInitializeBorder", at = @At("HEAD"))
    private void scenecraft$initStart(ClientboundInitializeBorderPacket packet, CallbackInfo ci) {
        BorderReplayTime.pending = FullReplaySender.borderPacketTime(packet);
    }

    @Inject(method = "handleInitializeBorder", at = @At("RETURN"))
    private void scenecraft$initEnd(ClientboundInitializeBorderPacket packet, CallbackInfo ci) {
        BorderReplayTime.pending = -1L;
        FullReplaySender.forgetBorderPacket(packet);
    }
}
