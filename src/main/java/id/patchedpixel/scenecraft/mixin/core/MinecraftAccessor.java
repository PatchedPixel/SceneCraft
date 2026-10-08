package id.patchedpixel.scenecraft.mixin.core;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("deltaTracker")
    DeltaTracker.Timer getTimer();
    @Accessor("deltaTracker") @Mutable
    void setTimer(DeltaTracker.Timer value);

    @Accessor("pendingReload")
    CompletableFuture<Void> getResourceReloadFuture();
    @Accessor("pendingReload")
    void setResourceReloadFuture(CompletableFuture<Void> value);

    @Accessor("pendingConnection")
    void setConnection(Connection connection);
}
