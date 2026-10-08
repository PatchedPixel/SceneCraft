package id.patchedpixel.scenecraft.mixin.record;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientHandshakePacketListenerImpl.class)
public interface ClientLoginNetworkHandlerAccessor {
    @Accessor("serverData")
    ServerData getServerInfo();
}
