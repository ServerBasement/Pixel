package it.ohalee.pixel.match;

import it.ohalee.pixel.SubPixel;
import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.util.Basement;
import org.redisson.api.map.event.EntryCreatedListener;
import org.redisson.api.map.event.EntryEvent;

import java.util.concurrent.CompletableFuture;

public abstract class IncomingSharedListener<E extends Enum<E> & PixelType, T extends SharedMatch, C extends Match<E, T>> implements EntryCreatedListener<String, String> {

    @Override
    public void onCreated(EntryEvent<String, String> event) {
        T shared = Basement.rclient().getLiveObjectService().get(getMatchClass(), event.getKey());
        instantiate(shared, event.getKey(), event.getValue()).whenCompleteAsync((match, throwable) -> {
                    if (throwable != null)
                        throwable.printStackTrace();

                    SubPixel.<E, T, C>getRaw().getMatchManager().putMatch(match);
                    match.processFill();
                }
        );
    }

    public abstract Class<T> getMatchClass();

    public abstract CompletableFuture<C> instantiate(T shared, String key, String value);

}
