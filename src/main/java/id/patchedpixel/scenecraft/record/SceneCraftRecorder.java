package id.patchedpixel.scenecraft.record;

import id.patchedpixel.scenecraft.core.KeyBindingRegistry;
import id.patchedpixel.scenecraft.core.Module;
import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.core.utils.Restrictions;
import id.patchedpixel.scenecraft.core.versions.MCVer.Keyboard;
import id.patchedpixel.scenecraft.record.handler.ConnectionEventHandler;
import id.patchedpixel.scenecraft.record.handler.GuiHandler;
import id.patchedpixel.scenecraft.record.packet.PacketListener;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SceneCraftRecorder implements Module {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AttributeKey<Void> ATTR_CHECKED = AttributeKey.newInstance("SceneCraftRecording_checked");

    { instance = this; }
    public static SceneCraftRecorder instance;

    private SceneCraft core;

    private ConnectionEventHandler connectionEventHandler;

    public SceneCraftRecorder(SceneCraft mod) {
        core = mod;

        core.getSettingsRegistry().register(Setting.class);
    }

    @Override
    public void registerKeyBindings(KeyBindingRegistry registry) {
        registry.registerKeyBinding("scenecraft.input.marker", Keyboard.KEY_M, new Runnable() {
            @Override
            public void run() {
                PacketListener packetListener = connectionEventHandler.getPacketListener();
                if (packetListener != null) {
                    packetListener.addMarker(null);
                    core.printInfoToChat("scenecraft.chat.addedmarker");
                }
            }
        }, false);
    }

    @Override
    public void initClient() {
        connectionEventHandler = new ConnectionEventHandler(LOGGER, core);

        new GuiHandler(core).register();

    }

    /** Registers the (ignored) restrictions payload so the connection doesn't reject it. */
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional()
                .configurationToClient(Restrictions.ID, Restrictions.CODEC, (payload, context) -> {})
                .playToClient(Restrictions.ID, Restrictions.CODEC, (payload, context) -> {})
                // Synthetic packet we inject into recordings (see MixinNetHandlerConfigClient); only ever seen during replay.
                .configurationToClient(ENABLED_PACKS_DATA, ENABLED_PACKS_CODEC, (payload, context) -> {});
    }

    private static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<EnabledPacksPayload> ENABLED_PACKS_DATA =
            new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(
                    net.minecraft.resources.Identifier.parse(id.patchedpixel.scenecraft.scenestudio.protocol.packets.PacketEnabledPacksData.ID));
    private static final net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.FriendlyByteBuf, EnabledPacksPayload> ENABLED_PACKS_CODEC =
            net.minecraft.network.codec.StreamCodec.of(
                    (buf, payload) -> buf.writeBytes(payload.bytes()),
                    buf -> {
                        byte[] bytes = new byte[buf.readableBytes()];
                        buf.readBytes(bytes);
                        return new EnabledPacksPayload(bytes);
                    });

    private record EnabledPacksPayload(byte[] bytes) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        @Override
        public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
            return ENABLED_PACKS_DATA;
        }
    }

    public void initiateRecording(Connection networkManager) {
        Channel channel = networkManager.channel;
        if (channel.pipeline().get(ReplayHandler.PACKET_HANDLER_NAME) != null) return;
        if (channel.hasAttr(ATTR_CHECKED)) return;
        channel.attr(ATTR_CHECKED).set(null);
        connectionEventHandler.onConnectedToServerEvent(networkManager);
    }

    public ConnectionEventHandler getConnectionEventHandler() {
        return connectionEventHandler;
    }
}
