package id.patchedpixel.scenecraft.render.hooks;

import id.patchedpixel.scenecraft.render.gui.progress.VirtualWindow;
import net.minecraft.client.Minecraft;

public interface WindowDelegateHolder {
    void setWindowDelegate(VirtualWindow window);

    static WindowDelegateHolder get(Minecraft mc) {
        return (WindowDelegateHolder) mc;
    }
}
