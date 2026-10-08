package id.patchedpixel.scenecraft.record;

import id.patchedpixel.scenecraft.core.KeyBindingRegistry;
import id.patchedpixel.scenecraft.core.Module;
import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.core.utils.Restrictions;
import id.patchedpixel.scenecraft.core.versions.MCVer.Keyboard;
import id.patchedpixel.scenecraft.record.handler.ConnectionEventHandler;
import id.patchedpixel.scenecraft.record.handler.GuiHandler;
import id.patchedpixel.scenecraft.mixin.record.NetworkManagerAccessor;
import id.patchedpixel.scenecraft.record.packet.PacketListener;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SceneCraftRecording implements Module {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AttributeKey<Void> ATTR_CHECKED = AttributeKey.newInstance("ReplayModRecording_checked");

    { instance = this; }
    public static SceneCraftRecording instance;

    private SceneCraft core;

    private ConnectionEventHandler connectionEventHandler;

    public SceneCraftRecording(SceneCraft mod) {
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
                .playToClient(Restrictions.ID, Restrictions.CODEC, (payload, context) -> {});
    }

    public void initiateRecording(Connection networkManager) {
        Channel channel = ((NetworkManagerAccessor) networkManager).getChannel();
        if (channel.pipeline().get(ReplayHandler.PACKET_HANDLER_NAME) != null) return;
        if (channel.hasAttr(ATTR_CHECKED)) return;
        channel.attr(ATTR_CHECKED).set(null);
        connectionEventHandler.onConnectedToServerEvent(networkManager);
    }

    public ConnectionEventHandler getConnectionEventHandler() {
        return connectionEventHandler;
    }
}
