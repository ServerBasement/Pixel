package it.ohalee.pixel.player;

import it.ohalee.pixel.api.Participator;
import it.ohalee.pixel.api.Queue;
import it.ohalee.pixel.stats.Statistics;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class PixelParticipator implements Participator {

    private final Player player;
    protected Queue queue;
    private Statistics statistics;
    private Long lastUpdate;

    public PixelParticipator(Player player) {
        this.player = player;
    }

    @Override
    public void assign(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public void queue(Queue queue) {
        if (this.queue != null)
            removeFromQueue();

        this.queue = queue;
        this.lastUpdate = System.currentTimeMillis();

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

    @Override
    public UUID key() {
        return player.getUniqueId();
    }

}
