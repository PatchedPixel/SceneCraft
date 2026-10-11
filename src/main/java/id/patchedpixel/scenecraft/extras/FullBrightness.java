package id.patchedpixel.scenecraft.extras;

import id.patchedpixel.scenecraft.core.SceneCraft;
import id.patchedpixel.scenecraft.core.events.PostRenderCallback;
import id.patchedpixel.scenecraft.core.events.PreRenderCallback;
import id.patchedpixel.scenecraft.core.versions.MCVer.Keyboard;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.replay.SceneCraftReplay;
import id.patchedpixel.scenecraft.replay.events.ReplayOpenedCallback;
import id.patchedpixel.scenecraft.replay.gui.overlay.GuiReplayOverlay;
import id.patchedpixel.scenecraft.gui.element.GuiImage;
import id.patchedpixel.scenecraft.gui.element.IGuiImage;
import id.patchedpixel.scenecraft.gui.layout.HorizontalLayout;
import id.patchedpixel.scenecraft.gui.utils.EventRegistrations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullBrightness extends EventRegistrations implements Extra {
    private SceneCraft core;
    private SceneCraftReplay module;

    private final IGuiImage indicator = new GuiImage().setTexture(SceneCraft.TEXTURE, 90, 20, 19, 16).setSize(19, 16);

    private Minecraft mc;
    private boolean active;
    private double originalGamma;

    @Override
    public void register(final SceneCraft mod) throws Exception {
        this.core = mod;
        this.module = SceneCraftReplay.instance;
        this.mc = mod.getMinecraft();

        mod.getKeyBindingRegistry().registerKeyBinding("scenecraft.input.lighting", Keyboard.KEY_Z, new Runnable() {
            @Override
            public void run() {
                active = !active;
                // need to tick once to update lightmap when replay is paused
                mod.getMinecraft().gameRenderer.tick();
                ReplayHandler replayHandler = module.getReplayHandler();
                if (replayHandler != null) {
                    updateIndicator(replayHandler.getOverlay());
                }
            }
        }, true);

        register();
    }

    public Type getType() {
        String str = core.getSettingsRegistry().get(Setting.FULL_BRIGHTNESS);
        for (Type type : Type.values()) {
            if (type.toString().equals(str)) {
                return type;
            }
        }
        return Type.Gamma;
    }

    { on(PreRenderCallback.EVENT, this::preRender); }
    private void preRender() {
        if (active && module.getReplayHandler() != null) {
            Type type = getType();
            if (type == Type.Gamma || type == Type.Both) {
                originalGamma = mc.options.gamma().get();
                ((id.patchedpixel.scenecraft.mixin.core.SimpleOptionAccessor<Double>) (Object) mc.options.gamma()).setRawValue(1000.0);
            }
            if (type == Type.NightVision || type == Type.Both) {
                if (mc.player != null) {
                    mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION
                            , Integer.MAX_VALUE));
                }
            }
        }
    }

    { on(PostRenderCallback.EVENT, this::postRender); }
    private void postRender() {
        if (active && module.getReplayHandler() != null) {
            Type type = getType();
            if (type == Type.Gamma || type == Type.Both) {
                ((id.patchedpixel.scenecraft.mixin.core.SimpleOptionAccessor<Double>) (Object) mc.options.gamma()).setRawValue(originalGamma);
            }
            if (type == Type.NightVision || type == Type.Both) {
                if (mc.player != null) {
                    mc.player.removeEffect(MobEffects.NIGHT_VISION
                    );
                }
            }
        }
    }

    { on(ReplayOpenedCallback.EVENT, replayHandler -> updateIndicator(replayHandler.getOverlay())); }
    private void updateIndicator(GuiReplayOverlay overlay) {
        if (active) {
            overlay.statusIndicatorPanel.addElements(new HorizontalLayout.Data(1), indicator);
        } else {
            overlay.statusIndicatorPanel.removeElement(indicator);
        }
    }

    enum Type {
        Gamma,
        NightVision,
        Both;

        @Override
        public String toString() {
            return "scenecraft.gui.settings.fullbrightness." + name().toLowerCase();
        }
    }
}
