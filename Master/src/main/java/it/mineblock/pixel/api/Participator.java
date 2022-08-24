package it.mineblock.pixel.api;

import it.mineblock.pixel.api.Queue;
import org.bukkit.entity.Player;

public interface Participator {

    void setQueue(Queue<?, ?, Participator> queue);
    void removeFromQueue();
    Player getPlayer();

}
