package id.patchedpixel.scenecraft.mixin.gui;

import id.patchedpixel.scenecraft.gui.versions.ScreenExt;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
public abstract class Mixin_PassEventsExt implements ScreenExt {
    @Unique
    private boolean passEvents;

    @Override
    public boolean doesPassEvents() {
        return this.passEvents;
    }

    @Override
    public void setPassEvents(boolean passEvents) {
        this.passEvents = passEvents;
    }
}
