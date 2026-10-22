package id.patchedpixel.scenecraft.core.events;

import com.mojang.blaze3d.platform.InputConstants;
import id.patchedpixel.scenecraft.gui.function.KeyInput;
import id.patchedpixel.scenecraft.gui.utils.Event;

public interface KeyEventCallback {
    Event<KeyEventCallback> EVENT = Event.create((listeners) ->
            (keyInput, action) -> {
                for (KeyEventCallback listener : listeners) {
                    if (listener.onKeyEvent(keyInput, action)) {
                        return true;
                    }
                }
                return false;
            }
    );

    int ACTION_PRESS = InputConstants.PRESS;

    boolean onKeyEvent(KeyInput keyInput, int action);
}
