package it.ohalee.pixel;

import it.ohalee.pixel.api.Participator;
import it.ohalee.pixel.api.Queue;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.player.PixelParticipatorManager;
import it.ohalee.pixel.server.ServerRancher;
import it.ohalee.pixel.server.handler.MasterSwitchMessage;
import it.ohalee.pixel.server.statistics.PixelStatistics;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.Basement;
import lombok.Getter;

import java.util.HashMap;

public class PixelProxy<
        T extends Enum<T> & PixelType,
        S extends SharedMatch,
        P extends Participator,
        V extends Queue<T, S, P>
        > {

    @Getter
    protected static PixelProxy rawProxy;
    @Getter
    private static PixelStatistics statistics;

    private final HashMap<T, V> queues = new HashMap<>();
    @Getter
    private ServerRancher<T, S> rancher;
    @Getter
    private PixelParticipatorManager playerManager;

    public V getQueue(T key) {
        return queues.get(key);
    }

    protected void insertQueue(T key, V queue) {
        queue.setRancher(rancher);
        queues.put(key, queue);
        queue.loadShared();
    }

    protected void setRancher(ServerRancher<T, S> rancher) {
        this.rancher = rancher;
    }

    protected void setPixelParticipatorManager(PixelParticipatorManager playerManager) {
        this.playerManager = playerManager;
    }

    protected <C extends Enum<C> & StatsType> void setStatistics(PixelStatistics<C> statistics) {
        PixelProxy.statistics = statistics;
    }

    public void shutdown() {
        MasterSwitchMessage switchMessage = rancher.unload();
        if (switchMessage == null) return;
        Basement.redis().publishMessage(switchMessage);
    }

    public Class<T> typeClass() {
        return rancher.typeClass();
    }
}
