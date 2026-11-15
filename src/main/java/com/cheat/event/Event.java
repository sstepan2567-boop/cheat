package com.cheat.event;

/**
 * Base class for every event posted on the {@link EventBus}.
 * An event can be cancelled; cancelled packet events stop the packet from being sent
 * or from being processed by the game.
 */
public class Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
