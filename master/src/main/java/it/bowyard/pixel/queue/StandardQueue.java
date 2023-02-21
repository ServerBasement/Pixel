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
import org.bukkit.scheduler.BukkitRunnable;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RMapCache;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class StandardQueue<E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends PixelParticipator> implements Queue<E, T, P> {

    protected final String mode;
    protected final E queueType;
    protected final MapSupplier mapSupplier;
    protected ServerRancher<E, T> rancher;

    @Override
    public void setRancher(ServerRancher<E, T> rancher) {
        this.rancher = rancher;
    }

    @Override
    public ServerRancher<E, T> getRancher() {
        return rancher;
    }

    protected QueueStatus status;

    protected final RMapCache<String, T> tunnels;
    protected final Deque<P> players = new ArrayDeque<>();

    public StandardQueue(String mode, E queueType, MapSupplier supplier) {
        this.mode = mode;
        this.queueType = queueType;
        this.mapSupplier = supplier;
        tunnels = summonTunnel();
    }

    public RMapCache<String, T> summonTunnel() {
        return Basement.rclient().getMapCache(mode + "_" + queueType.name() + "_tunnels");
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
            Optional<InternalServer<E, T>> optInternal = rancher.get(match.getServer());
            if (optInternal.isEmpty() || optInternal.get().removeMatch(matchName) == null)
                dropMatch(match);
        }
        Pixel.LOGGER.info("[Queue " + queueType.toString() + "] Found and clearing " + died.size() + " died matches.");
    }

    @Override
    public T initMatch() {
        return rancher.seekServer().map(etInternalServer -> this.initMatch(etInternalServer, mapSupplier.getMap(this.queueType))).orElse(null);
    }

    public T initMatch(String mapName) {
        return rancher.seekServer().map(etInternalServer -> this.initMatch(etInternalServer, mapName)).orElse(null);
    }

    public T initMatch(InternalServer<E, T> server, String mapName) {
        if (mapName == null) return null;
        server.loadingMatch(true);

        // Match creation
        T match = summonMatch();
        match.setServer(server.getServer().getName());
        match.setStatus(SharedMatchStatus.OPEN);
        match.setRequired(queueType.teams() * queueType.teamSize());
        match.setTeamSize(queueType.teamSize());
        match.setTeamsNumber(queueType.teams());
        match.setEffectivePlayers(0);
        match.setJoiningPlayers(0);
        match.setMap(mapName);

        T shared = Basement.rclient().getLiveObjectService().merge(match);
        server.addMatch(shared.getName(), match.getMap());
        return shared;
    }

    @Override
    public void validateMatch(T match) {
        tunnels.fastPut(match.getName(), match);
        Pixel.LOGGER.info("[Queue " + queueType.toString() + "] Match " + match.getName() + " validated: " + System.currentTimeMillis());
    }

    @Override
    public T createMatch() {
        T match = initMatch();
        if (match == null) Pixel.LOGGER.warning("[Queue " + queueType.toString() + "] Failed to create match.");
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
            if (match.getStatus() == SharedMatchStatus.WAITING_LAST)
                continue;
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

    public void tillEOL() {
        StaticTask.runBukkitTaskTimer(new BukkitRunnable() {
            @Override
            public void run() {
                if (tunnels == null) {
                    cancel();
                    return;
                }
                for (T value : tunnels.values()) {
                    if (value.getStatus() != SharedMatchStatus.WAITING_LAST) continue;
                    value.warranty();
                }
            }
        }, 20L*3, 20L*3, true);
    }

}
