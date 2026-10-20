package id.patchedpixel.scenecraft.gui.function;

import net.minecraft.util.Util;

import org.lwjgl.sdl.SDLKeycode;

public interface InputWithModifiers {
    int modifiers();

    default boolean hasCtrl() {
        return (modifiers() & _CTRL_MOD) != 0;
    }
    default boolean hasShift() {
        return (modifiers() & _SHIFT_MOD) != 0;
    }
    default boolean hasAlt() {
        return (modifiers() & _ALT_MOD) != 0;
    }

    // The modifiers of an input event are a mask of SDL_KMOD_* values (either side of a modifier key counts).
    // On macOS the "command" key (GUI) takes the role of control.
    int _CTRL_MOD = Util.getPlatform() == Util.OS.OSX ? SDLKeycode.SDL_KMOD_GUI : SDLKeycode.SDL_KMOD_CTRL;
    int _SHIFT_MOD = SDLKeycode.SDL_KMOD_SHIFT;
    int _ALT_MOD = SDLKeycode.SDL_KMOD_ALT;
}
