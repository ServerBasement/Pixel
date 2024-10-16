package it.ohalee.pixel.api;

import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.server.ServerRancher;
import org.redisson.api.RMapCache;

import java.util.Optional;
import java.util.Set;

public interface Queue<E extends Enum<E> & PixelType, T extends SharedMatch, P extends Participator> {

    E getType();

    ServerRancher<E, T> getRancher();

    void setRancher(ServerRancher<E, T> rancher);

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

    QueueStealer summonStealer();

    void tillEOL();

}
