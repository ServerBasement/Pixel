package it.bowyard.pixel;

import it.bowyard.pixel.api.Match;
import it.bowyard.pixel.match.PixelMatchManager;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.player.PlayerReceiver;
import it.bowyard.pixel.topics.ShutdownHandler;
import it.bowyard.pixel.topics.StatusHandler;
import lombok.Getter;

public abstract class SubPixel<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> {

    public static void process(SubPixel<?, ?, ?> rawParent) {
        raw = rawParent;
    }

    protected static SubPixel<?, ?, ?> raw;

    public static <SE extends Enum<SE> & PixelType, ST extends SharedMatch<SE>, SC extends Match<SE, ST>> SubPixel<SE, ST, SC> getRaw() {
        return (SubPixel<SE, ST, SC>) raw;
    }

    @Getter
    private final PixelMatchManager<E, T, C> matchManager;

    abstract PixelMatchManager<E, T, C> summonMatchManager();

    @Getter
    private final PlayerReceiver<E, T, C> playerReceiver;

    abstract PlayerReceiver<E, T, C> summonPlayerReceiver();

    protected SubPixel() {
        new ShutdownHandler();
        new StatusHandler();
        matchManager = summonMatchManager();
        playerReceiver = summonPlayerReceiver();
    }

}
