package id.patchedpixel.scenecraft.core.events;

import id.patchedpixel.scenecraft.gui.utils.Event;

public interface KeyBindingEventCallback {
    Event<KeyBindingEventCallback> EVENT = Event.create((listeners) ->
            () -> {
                for (KeyBindingEventCallback listener : listeners) {
                    listener.onKeybindingEvent();
                }
            }
    );

    void onKeybindingEvent();
}
