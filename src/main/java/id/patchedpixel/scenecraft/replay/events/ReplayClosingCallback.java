package id.patchedpixel.scenecraft.replay.events;

import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.gui.utils.Event;

import java.io.IOException;

public interface ReplayClosingCallback {
    Event<ReplayClosingCallback> EVENT = Event.create((listeners) ->
            (replayHandler) -> {
                for (ReplayClosingCallback listener : listeners) {
                    listener.replayClosing(replayHandler);
                }
            });

    void replayClosing(ReplayHandler replayHandler) throws IOException;
}
