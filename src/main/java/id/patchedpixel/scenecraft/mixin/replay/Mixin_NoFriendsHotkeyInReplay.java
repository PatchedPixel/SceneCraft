package id.patchedpixel.scenecraft.mixin.replay;

import com.mojang.blaze3d.platform.InputConstants;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Since 26.3 vanilla has a global "Friends" hotkey (default: O) which is handled in
 * {@code Minecraft#handleGlobalKeyPress} before any keybinding or screen, so the usual conflict resolution
 * (see Mixin_ContextualKeyBindings) cannot override it. It collides with SceneCraft's "O" (time keyframe),
 * so while a replay is open we let the key pass through instead of opening the friends overlay.
 */
@Mixin(Minecraft.class)
public class Mixin_NoFriendsHotkeyInReplay {
    @Inject(method = "handleGlobalKeyPress", at = @At("HEAD"), cancellable = true)
    private void scenecraft$noFriendsHotkeyInReplay(InputConstants.Key key, boolean controlDown, CallbackInfoReturnable<Boolean> cir) {
        SceneCraftReplay replay = SceneCraftReplay.instance;
        if (replay == null || replay.getReplayHandler() == null) {
            return;
        }
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.options.keyFriends.matches(key)) {
            cir.setReturnValue(false);
        }
    }
}
