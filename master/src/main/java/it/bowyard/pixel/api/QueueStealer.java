package it.bowyard.pixel.api;

import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class QueueStealer
        <E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends Participator, Q extends Queue<E, T, P>>
        extends BukkitRunnable {
}
