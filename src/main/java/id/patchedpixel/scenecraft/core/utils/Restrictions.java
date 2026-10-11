package id.patchedpixel.scenecraft.core.utils;

import id.patchedpixel.scenecraft.core.SceneCraft;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import static id.patchedpixel.scenecraft.gui.versions.MCVer.identifier;
import io.netty.buffer.Unpooled;

/**
 * Restrictions set by the server,
 * @see <a href="https://gist.github.com/Johni0702/2547c463e51f65f312cb">Replay Restrictions Gist</a>
 */
public class Restrictions {
    public static final Identifier PLUGIN_CHANNEL = identifier(SceneCraft.MOD_ID, "restrict");
    private boolean noXray;
    private boolean noNoclip;
    private boolean onlyFirstPerson;
    private boolean onlyRecordingPlayer;

    public String handle(ClientboundCustomPayloadPacket packet) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(((Payload) packet.payload()).bytes()));
        while (buffer.isReadable()) {
            String name = buffer.readUtf(64);
            boolean active = buffer.readBoolean();
                return name;
        }

        return null;
    }

    public boolean isNoXray() {
        return noXray;
    }

    public boolean isNoNoclip() {
        return noNoclip;
    }

    public boolean isOnlyFirstPerson() {
        return onlyFirstPerson;
    }

    public boolean isOnlyRecordingPlayer() {
        return onlyRecordingPlayer;
    }

    public static final CustomPacketPayload.Type<Payload> ID = new CustomPacketPayload.Type<>(PLUGIN_CHANNEL);
    public static final StreamCodec<? super FriendlyByteBuf, Payload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBytes(payload.bytes()),
            buf -> {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                return new Payload(bytes);
            }
    );
    public record Payload(byte[] bytes) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
