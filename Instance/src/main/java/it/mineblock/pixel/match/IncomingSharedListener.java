package it.mineblock.pixel.match;

import it.mineblock.pixel.SubPixel;
import it.mineblock.pixel.api.Match;
import it.mineblock.pixel.util.Basement;
import org.redisson.api.map.event.EntryCreatedListener;
import org.redisson.api.map.event.EntryEvent;

public abstract class IncomingSharedListener<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> implements EntryCreatedListener<String, String> {

    @Override
    public void onCreated(EntryEvent<String, String> event) {
        T shared = (T) Basement.rclient().getLiveObjectService().get(SharedMatch.class, event.getKey());
        SubPixel.<E, T, C>getRaw().getMatchManager().putMatch(instantiate(shared));
    }

    abstract C instantiate(T shared);

}
