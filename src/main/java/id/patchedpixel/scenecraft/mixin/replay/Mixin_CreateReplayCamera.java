package id.patchedpixel.scenecraft.mixin.replay;

import id.patchedpixel.scenecraft.replay.ReplayModReplay;
import id.patchedpixel.scenecraft.replay.camera.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Input;

import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.stats.StatsCounter;

import net.minecraft.client.player.LocalPlayer;

@Mixin(MultiPlayerGameMode.class)
public abstract class Mixin_CreateReplayCamera {

    @Shadow
    private Minecraft minecraft;

    @Shadow
    private ClientPacketListener connection;

    @Inject(method = "createPlayer(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/stats/StatsCounter;Lnet/minecraft/client/ClientRecipeBook;Lnet/minecraft/world/entity/player/Input;ZLnet/minecraft/client/player/ItemActivation;)Lnet/minecraft/client/player/LocalPlayer;", at=@At("HEAD"), cancellable = true)
    private void replayModReplay_createReplayCamera(
            ClientLevel worldIn,
            StatsCounter statisticsManager,
            ClientRecipeBook recipeBookClient,
            Input playerInput,
            boolean lastSprinting,
            net.minecraft.client.player.ItemActivation itemActivation,
            CallbackInfoReturnable<LocalPlayer> ci
    ) {
        if (ReplayModReplay.instance.getReplayHandler() != null) {
            ci.setReturnValue(new CameraEntity(this.minecraft, worldIn, this.connection, statisticsManager, recipeBookClient, itemActivation));
            ci.cancel();
        }
    }
}
