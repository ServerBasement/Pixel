package it.bowyard.pixel.api;

import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.queue.StandardQueueStealer;
import it.bowyard.pixel.server.ServerRancher;
import org.redisson.api.RMapCache;

import java.util.Optional;
import java.util.Set;

public interface Queue<E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends Participator> {

    E getType();
    void setRancher(ServerRancher rancher);
    ServerRancher getRancher();
    RMapCache<String, T> summonTunnel();

    void loadShared();

    T initMatch();
    void validateMatch(T match);

    T createMatch();
    void dropMatch(String name);
    void dropMatch(T match);
    T getMatch(String name);
    Set<T> getMatches();
    T seekMatch(int weight);
    T summonMatch();

    int matchLoad();
    int playerLoad();
    int queuePlayerLoad();

    void addPlayer(P participator, boolean last);
    void removePlayer(P participator);
    Optional<P> stealPlayer();

    void idle();
    void task();
    StandardQueueStealer summonStealer();

}
