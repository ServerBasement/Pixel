package it.bowyard.pixel.api;

import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.match.SharedMatchStatus;
import it.bowyard.pixel.player.PlayerReceiver;
import it.bowyard.pixel.topics.ValidateRequest;
import it.bowyard.pixel.util.Basement;
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
    protected final RMapCache<String, String> shared_spectators;

    @Getter
    protected E type;

    protected int listenerId;

    public Match(T shared) {
        this.shared = shared;
        this.type = E.valueOf(shared.typeClass(), shared.getType());
        joining = Basement.rclient().getMapCache(shared.getName() + "_joining");
        shared_spectators = Basement.rclient().getMapCache(shared.getName() + "_spectators");
    }

    public void processFill() {
        System.out.println("Started ProcessFill: " + shared.getName() + " " + System.currentTimeMillis());

        joining.forEach(PlayerReceiver::addJoining);
        listenerId = joining.addListener((EntryCreatedListener<String, String>) event -> PlayerReceiver.addJoining(event.getKey(), event.getValue()));
        shared_spectators.addListener((EntryCreatedListener<String, String>) event -> PlayerReceiver.addJoining(event.getKey(), event.getValue()));

        // TRY FIX 2.0
        validateMatch();

        System.out.println("Finished ProcessFill: " + shared.getName() + " " + joining.keySet().toString() + " " + System.currentTimeMillis());
    }

    public void stopProcessFill() {
        joining.removeListener(listenerId);
    }

    public void warranty(SharedMatchStatus status) {
        shared.setJoiningPlayers(0);
        shared.setEffectivePlayers(getMatchWeight());
        joining.forEach((p, m) -> PlayerReceiver.removeJoining(p));
        joining.clear();
        shared.setStatus(status);
    }

    public void validateMatch() {
        System.out.println("Validating Match " + shared.getName());
        Basement.redis().publishMessage(new ValidateRequest(shared.getServer(), shared.getName()));
    }

    abstract public String getWorldName();
    abstract public void letJoin(Player player);
    abstract public void letQuit(Player player);
    abstract public int getMatchWeight();

}
