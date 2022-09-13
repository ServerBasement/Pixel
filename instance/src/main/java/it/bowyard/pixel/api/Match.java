package it.bowyard.pixel.api;

import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.match.SharedMatchStatus;
import it.bowyard.pixel.player.PlayerReceiver;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryCreatedListener;

public abstract class Match<E extends Enum<E> & PixelType, T extends SharedMatch<E>> {

    @Getter
    protected final T shared;
    @Getter
    protected final RMapCache<String, String> joining;

    @Getter
    protected E type;

    protected int listenerId;

    public Match(T shared) {
        this.shared = shared;
        this.type = E.valueOf(shared.typeClass(), shared.getType());
        joining = Basement.rclient().getMapCache(shared.getName() + "_joining");
    }

    public void processFill() {
        joining.forEach(PlayerReceiver::addJoining);
        listenerId = joining.addListener((EntryCreatedListener<String, String>) event -> PlayerReceiver.addJoining(event.getKey(), event.getValue()));
    }

    public void stopProcessFill() {
        joining.removeListener(listenerId);
    }

    public void warranty(SharedMatchStatus status) {
        shared.setStatus(status);
        shared.setJoiningPlayers(0);
        shared.setEffectivePlayers(getMatchWeight());
        joining.forEach((p, m) -> PlayerReceiver.removeJoining(p));
        joining.clear();
    }

    abstract public void letJoin(Player player);
    abstract public void letQuit(Player player);
    abstract public int getMatchWeight();

}
