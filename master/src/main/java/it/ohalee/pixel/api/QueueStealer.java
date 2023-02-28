package it.ohalee.pixel.api;

import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class QueueStealer
        <E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends Participator, Q extends Queue<E, T, P>>
        extends BukkitRunnable {
}
