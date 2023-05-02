package it.ohalee.pixel.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface Participator extends PixelUser<UUID> {

    void queue(Queue<?, ?, Participator> queue);

    void removeFromQueue();

    Player getPlayer();

}
