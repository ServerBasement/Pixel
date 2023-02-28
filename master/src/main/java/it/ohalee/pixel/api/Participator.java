package it.ohalee.pixel.api;

import org.bukkit.entity.Player;

public interface Participator {

    void setQueue(Queue<?, ?, Participator> queue);

    void removeFromQueue();

    Player getPlayer();

}
