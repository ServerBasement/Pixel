package it.bowyard.pixel.player;

import it.bowyard.pixel.api.Participator;
import it.bowyard.pixel.api.Queue;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class PixelParticipator implements Participator {

    private final Player player;
    private Queue<?, ?, Participator> queue;

    public void setQueue(Queue<?, ?, Participator> queue) {
        if (this.queue != null)
            removeFromQueue();
        this.queue = queue;
        queue.addPlayer(this, true);
    }

    public void removeFromQueue() {
        if (queue == null) return;
        queue.removePlayer(this);
        queue = null;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

}
