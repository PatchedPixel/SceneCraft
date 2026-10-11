package id.patchedpixel.scenecraft.mixin.record;

import id.patchedpixel.scenecraft.core.versions.MCVer;
import id.patchedpixel.scenecraft.record.SceneCraftRecorder;
import id.patchedpixel.scenecraft.record.handler.RecordingEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class MixinNetHandlerPlayClient {

    // The stupid name is required as otherwise Mixin treats it as a shadow, seemingly ignoring the lack of @Shadow
    private static Minecraft mcStatic = MCVer.getMinecraft();

    @Shadow
    private Map<UUID, PlayerInfo> playerInfoMap;

    /**
     * Record the own player entity joining the world.
     * We cannot use the {@link net.minecraftforge.event.entity.EntityJoinWorldEvent} because the entity id
     * of the player is set afterwards and the tablist entry might not yet be sent.
     * @param packet The packet
     * @param ci Callback info
     */
    @Inject(method = "handlePlayerInfoUpdate", at=@At("HEAD"))
    public void recordOwnJoin(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (!mcStatic.isSameThread()) return;
        if (mcStatic.player == null) return;

        RecordingEventHandler handler = SceneCraftRecorder.instance.getConnectionEventHandler().getRecordingEventHandler();
        if (handler != null && packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
                UUID uuid = entry.profileId();
                // Only add spawn packet for our own player and only if he isn't known yet
                if (uuid.equals(mcStatic.player.getGameProfile().id()) && !this.playerInfoMap.containsKey(uuid)) {
                    handler.spawnRecordingPlayer();
                }
            }
        }
    }

    /**
     * Record the own player entity respawning.
     * We cannot use the {@link net.minecraftforge.event.entity.EntityJoinWorldEvent} because that would also include
     * the first spawn which is already handled by the above method.
     * @param packet The packet
     * @param ci Callback info
     */
    @Inject(method = "handleRespawn", at=@At("RETURN"))
    public void recordOwnRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        RecordingEventHandler handler = SceneCraftRecorder.instance.getConnectionEventHandler().getRecordingEventHandler();
        if (handler != null) {
            handler.spawnRecordingPlayer();
        }
    }
}
