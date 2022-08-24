package it.mineblock.pixel.player;

import it.mineblock.pixel.api.Participator;
import it.mineblock.pixel.api.Queue;
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
