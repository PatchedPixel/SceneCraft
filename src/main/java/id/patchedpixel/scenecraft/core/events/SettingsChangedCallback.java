package id.patchedpixel.scenecraft.core.events;

import id.patchedpixel.scenecraft.core.SettingsRegistry;
import id.patchedpixel.scenecraft.gui.utils.Event;

public interface SettingsChangedCallback {
    Event<SettingsChangedCallback> EVENT = Event.create((listeners) ->
            (registry, key) -> {
                for (SettingsChangedCallback listener : listeners) {
                    listener.onSettingsChanged(registry, key);
                }
            }
    );

    void onSettingsChanged(SettingsRegistry registry, SettingsRegistry.SettingKey<?> key);
}
