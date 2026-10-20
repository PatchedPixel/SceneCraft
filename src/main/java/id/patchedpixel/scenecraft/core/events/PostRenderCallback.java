package id.patchedpixel.scenecraft.core.events;

import id.patchedpixel.scenecraft.gui.utils.Event;

public interface PostRenderCallback {
    Event<PostRenderCallback> EVENT = Event.create((listeners) ->
            () -> {
                for (PostRenderCallback listener : listeners) {
                    listener.postRender();
                }
            }
    );

    void postRender();
}
