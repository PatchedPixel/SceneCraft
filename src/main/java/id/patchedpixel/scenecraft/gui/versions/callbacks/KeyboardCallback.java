package id.patchedpixel.scenecraft.gui.versions.callbacks;

import id.patchedpixel.scenecraft.gui.function.CharInput;
import id.patchedpixel.scenecraft.gui.function.KeyInput;
import id.patchedpixel.scenecraft.gui.utils.Event;

public interface KeyboardCallback {
    Event<KeyboardCallback> EVENT = Event.create((listeners) ->
            new KeyboardCallback() {
                @Override
                public boolean keyPressed(KeyInput keyInput) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyPressed(keyInput)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean keyReleased(KeyInput keyInput) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.keyReleased(keyInput)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean charTyped(CharInput charInput) {
                    for (KeyboardCallback listener : listeners) {
                        if (listener.charTyped(charInput)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    boolean keyPressed(KeyInput keyInput);
    boolean keyReleased(KeyInput keyInput);
    boolean charTyped(CharInput charInput);
}
