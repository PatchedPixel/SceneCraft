package id.patchedpixel.scenecraft.replay.events;

import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.gui.utils.Event;

import java.io.IOException;

public interface ReplayOpenedCallback {
    Event<ReplayOpenedCallback> EVENT = Event.create((listeners) ->
            (replayHandler) -> {
                for (ReplayOpenedCallback listener : listeners) {
                    listener.replayOpened(replayHandler);
                }
            });

    void replayOpened(ReplayHandler replayHandler) throws IOException;
}
