package id.patchedpixel.scenecraft.replay.events;

import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.gui.utils.Event;

public interface ReplayClosedCallback {
    Event<ReplayClosedCallback> EVENT = Event.create((listeners) ->
            (replayHandler) -> {
                for (ReplayClosedCallback listener : listeners) {
                    listener.replayClosed(replayHandler);
                }
            });

    void replayClosed(ReplayHandler replayHandler);
}
