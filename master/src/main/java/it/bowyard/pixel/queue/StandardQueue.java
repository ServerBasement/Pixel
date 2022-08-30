package it.bowyard.pixel.queue;

import it.bowyard.pixel.Pixel;
import it.bowyard.pixel.api.MapSupplier;
import it.bowyard.pixel.api.Queue;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.match.SharedMatchStatus;
import it.bowyard.pixel.player.PixelParticipator;
import it.bowyard.pixel.server.InternalServer;
import it.bowyard.pixel.server.ServerRancher;
import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.util.StaticTask;
import org.redisson.api.RMapCache;

import java.util.*;

public abstract class StandardQueue<E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends PixelParticipator> implements Queue<E, T, P> {

    private final String mode;
    private final E queueType;
    private final MapSupplier mapSupplier;
    private ServerRancher rancher;

    @Override
    public void setRancher(ServerRancher rancher) {
        this.rancher = rancher;
    }

    @Override
    public ServerRancher getRancher() {
        return rancher;
    }

    private QueueStatus status;

    protected final RMapCache<String, T> tunnels;
    protected final Deque<P> players = new ArrayDeque<>();

    public StandardQueue(String mode, E queueType, MapSupplier supplier) {
        this.mode = mode;
        this.queueType = queueType;
        this.mapSupplier = supplier;
        tunnels = summonTunnel();
        loadShared();
    }

    public RMapCache<String, T> summonTunnel() {
        return Basement.rclient().getMapCache(mode + "_" + queueType.name() + "_tunnels"); // bridge_V1_tunnels
    }

    @Override
    public E getType() {
        return queueType;
    }

    @Override
    public void loadShared() {
        List<T> died = new ArrayList<>();
        for (T match : tunnels.values()) {
            if (match.getStatus() == SharedMatchStatus.DIED) {
                died.add(match);
            }
        }
        if (died.size() == 0) return;
        for (T match : died) {
            String matchName = match.getName();
            Optional<InternalServer> optInternal = rancher.get(match.getServer());
            if (optInternal.isEmpty() || optInternal.get().removeMatch(matchName) == null)
                dropMatch(match);
        }
        Pixel.LOGGER.info("[Queue " + queueType.toString() + "] Found and clearing " + died.size() + " died matches.");
    }

    @Override
    public T initMatch() {
        // Server Seeking
        Optional<InternalServer> oiServer = rancher.seekServer();
        if (oiServer.isEmpty()) return null;
        InternalServer server = oiServer.get();

        // Match creation
        //T match = (T) new SharedMatch<>(mode + UUID.randomUUID().toString().substring(0, 3) + UUID.randomUUID().toString().substring(1, 4), queueType);
        T match = summonMatch();
        match.setServer(server.getServer().getName());
        match.setStatus(SharedMatchStatus.OPEN);
        match.setRequired(queueType.teams() * queueType.teamSize());
        match.setEffectivePlayers(0);
        match.setJoiningPlayers(0);

        T shared = Basement.rclient().getLiveObjectService().merge(match);
        server.addMatch(shared.getName(), mapSupplier.getMap());
        return shared;
    }

    @Override
    public void validateMatch(T match) {
        tunnels.fastPut(match.getName(), match);
    }

    @Override
    public T createMatch() {
        T match = initMatch();
        validateMatch(match);
        return match;
    }

    @Override
    public void dropMatch(String name) {
        T match = tunnels.remove(name);
        if (match == null) return;
        Basement.rclient().getLiveObjectService().delete(match);
    }

    @Override
    public void dropMatch(T match) {
        tunnels.fastRemove(match.getName());
        Basement.rclient().getLiveObjectService().delete(match);
    }

    @Override
    public T getMatch(String name) {
        return tunnels.get(name);
    }

    @Override
    public Set<T> getMatches() {
        return new HashSet<>(tunnels.values());
    }


    @Override
    public T seekMatch(int weight) {
        if (tunnels.isEmpty()) return createMatch();
        for (T match : tunnels.values()) {
            if (match.getStatus() == SharedMatchStatus.WAITING_LAST) {
                match.warranty();
                continue;
            }
            if (match.getStatus() == SharedMatchStatus.OPEN) {
                if (!match.canCarry(weight)) continue;
                return match;
            }
        }
        return createMatch();
    }


    @Override
    public int matchLoad() {
        return tunnels.size();
    }

    @Override
    public int playerLoad() {
        return tunnels.values().parallelStream().mapToInt(T::totalCount).sum();
    }

    @Override
    public int queuePlayerLoad() {
        return players.size();
    }

    @Override
    public void idle() {
        status = QueueStatus.IDLE;
    }

    @Override
    public void task() {
        status = QueueStatus.TASK;
        StaticTask.runBukkitTaskTimer(summonStealer(), 2L, 2L, true);
    }

    @Override
    public void addPlayer(P participator, boolean last) {
        if (last)
            players.addLast(participator);
        else
            players.addFirst(participator);
        if (status == QueueStatus.TASK) return;
        task();
    }

    @Override
    public void removePlayer(P participator) {
        players.remove(participator);
    }

    @Override
    public Optional<P> stealPlayer() {
        return Optional.ofNullable(players.poll());
    }

    @Override
    public StandardQueueStealer summonStealer() {
        return new StandardQueueStealer(
                Basement.rclient().getLock("lock_" + queueType.name()),
                (StandardQueue<?, ?, PixelParticipator>) this
        );
    }
}
