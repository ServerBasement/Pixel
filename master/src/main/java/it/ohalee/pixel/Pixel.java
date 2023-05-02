package it.ohalee.pixel;

import it.ohalee.pixel.api.Participator;
import it.ohalee.pixel.api.Queue;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.player.PixelParticipatorManager;
import it.ohalee.pixel.server.ServerRancher;
import it.ohalee.pixel.server.ServerRancherConfiguration;
import it.ohalee.pixel.server.statistics.PixelStatistics;
import it.ohalee.pixel.server.statistics.ServerStatsConfiguration;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.Basement;
import it.ohalee.pixel.util.StaticTask;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class Pixel<T extends Enum<T> & PixelType, S extends SharedMatch, P extends Participator, V extends Queue<T, S, P>> {

    public static Logger LOGGER;
    @Setter
    public static boolean LEADER;

    private final PixelProxy<T, S, P, V> proxy = new PixelProxy<>();

    public Pixel(JavaPlugin plugin) {
        new StaticTask(plugin);
        Basement.init();
    }

    public PixelProxy<T, S, P, V> process() {
        PixelProxy.rawProxy = this.proxy;
        if (proxy.getPlayerManager() == null)
            throw new NullPointerException("PixelParticipatorManager is not registered.");
        proxy.getRancher().start();
        return this.proxy;
    }

    public Pixel<T, S, P, V> metadata(boolean isLeader) {
        LEADER = isLeader;
        return this;
    }

    public Pixel<T, S, P, V> registerLogger(Logger logger) {
        LOGGER = logger;
        return this;
    }

    public Pixel<T, S, P, V> registerRancher(JavaPlugin plugin, ServerRancherConfiguration<T, S> configuration) {
        proxy.setRancher(new ServerRancher<>(plugin, configuration));
        return this;
    }

    public Pixel<T, S, P, V> registerPlayerManager(PixelParticipatorManager playerManager) {
        proxy.setPixelParticipatorManager(playerManager);
        return this;
    }

    public <C extends Enum<C> & StatsType> Pixel<T, S, P, V> registerStatistics(ServerStatsConfiguration<C> configuration) {
        proxy.setStatistics(new PixelStatistics<>(configuration));
        return this;
    }

    public Pixel<T, S, P, V> registerQueue(V queue) {
        proxy.insertQueue(queue.getType(), queue);
        queue.tillEOL();
        return this;
    }

}
