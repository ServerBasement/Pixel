package it.ohalee.pixel.api;

import it.ohalee.pixel.SubPixel;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.match.SharedMatchStatus;
import it.ohalee.pixel.player.PlayerReceiver;
import it.ohalee.pixel.topics.ValidateRequest;
import it.ohalee.pixel.util.Basement;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryCreatedListener;

public abstract class Match<E extends Enum<E> & PixelType, T extends SharedMatch> {

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
        this.type = E.valueOf((Class<E>) SubPixel.getRaw().getMatchManager().typeClass(), shared.getType());
        joining = Basement.rclient().getMapCache(shared.getName() + "_joining");
        shared_spectators = Basement.rclient().getMapCache(shared.getName() + "_spectators");
    }

    public void processFill() {
        joining.forEach(PlayerReceiver::addJoining);

        listenerId = joining.addListener((EntryCreatedListener<String, String>) event -> PlayerReceiver.addJoining(event.getKey(), event.getValue()));
        shared_spectators.addListener((EntryCreatedListener<String, String>) event -> PlayerReceiver.addJoining(event.getKey(), event.getValue()));

        validateMatch();
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
        Basement.redis().publishMessage(new ValidateRequest(shared.getServer(), shared.getName()));
    }

    abstract public String getWorldName();

    abstract public void letJoin(Player player);

    abstract public void letQuit(Player player);

    abstract public int getMatchWeight();

}
