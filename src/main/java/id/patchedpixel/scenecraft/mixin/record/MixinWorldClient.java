package id.patchedpixel.scenecraft.mixin.record;

import id.patchedpixel.scenecraft.record.SceneCraftRecording;
import id.patchedpixel.scenecraft.record.handler.RecordingEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;

import net.minecraft.core.Holder;

@Mixin(ClientLevel.class)
public abstract class MixinWorldClient extends Level {
    @Shadow
    private Minecraft minecraft;

    @SuppressWarnings("ConstantConditions")
    protected MixinWorldClient() {
        super(null, null, null, null, false, false, 0, 0);
    }

    // Sounds that are emitted by thePlayer no longer take the long way over the server
    // but are instead played directly by the client. The server only sends these sounds to
    // other clients so we have to record them manually.
    // E.g. Block place sounds
    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At("HEAD"))
    public void sceneCraftRecording_recordClientSound(
            Entity player,
            double x, double y, double z,
            Holder<SoundEvent> sound,
            SoundSource category,
            float volume, float pitch,
            long seed,
            CallbackInfo ci) {
        if (player == this.minecraft.player) {
            RecordingEventHandler handler = SceneCraftRecording.instance.getConnectionEventHandler().getRecordingEventHandler();
            if (handler != null) {
                // Sent to all other players in ServerWorldEventHandler#playSoundToAllNearExcept
                handler.onPacket(new ClientboundSoundPacket(
                        sound, category, x, y, z, volume, pitch
                        , seed
                ));
            }
        }
    }

    // Same goes for level events (also called effects). E.g. door open, block break, etc.
    @Inject(method = "levelEvent", at = @At("HEAD"))
    private void playLevelEvent(
            Entity player,
            int type, BlockPos pos, int data, CallbackInfo ci
    ) {
        if (player == this.minecraft.player) {
            // We caused this event, the server won't send it to us
            RecordingEventHandler handler = SceneCraftRecording.instance.getConnectionEventHandler().getRecordingEventHandler();
            if (handler != null) {
                handler.onClientEffect(type, pos, data);
            }
        }
    }
}
