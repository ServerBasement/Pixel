package it.bowyard.pixel;

import it.bowyard.pixel.api.Match;
import it.bowyard.pixel.match.PixelMatchManager;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.player.PlayerReceiver;
import it.bowyard.pixel.topics.ShutdownHandler;
import it.bowyard.pixel.topics.StatusHandler;
import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.util.StaticTask;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SubPixel<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> {

    protected static SubPixel<?, ?, ?> raw;

    public static <SE extends Enum<SE> & PixelType, ST extends SharedMatch<SE>, SC extends Match<SE, ST>> SubPixel<SE, ST, SC> getRaw() {
        return (SubPixel<SE, ST, SC>) raw;
    }

    @Getter
    private final PixelMatchManager<E, T, C> matchManager;

    public abstract PixelMatchManager<E, T, C> summonMatchManager();

    @Getter
    private final PlayerReceiver<E, T, C> playerReceiver;

    public abstract PlayerReceiver<E, T, C> summonPlayerReceiver();

    public SubPixel(JavaPlugin plugin) {
        Basement.init();
        new StaticTask(plugin);
        new ShutdownHandler();
        new StatusHandler();
        matchManager = summonMatchManager();
        playerReceiver = summonPlayerReceiver();
        raw = this;
    }

}
