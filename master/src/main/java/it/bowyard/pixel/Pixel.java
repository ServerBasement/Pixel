package it.bowyard.pixel;

import it.bowyard.pixel.api.Participator;
import it.bowyard.pixel.api.Queue;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.server.ServerRancher;
import it.bowyard.pixel.server.ServerRancherConfiguration;
import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.util.StaticTask;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class Pixel <
        T extends Enum<T> & PixelType,
        S extends SharedMatch<T>,
        P extends Participator,
        V extends Queue<T, S, P>
        >  {

    public static Logger LOGGER;
    @Setter
    public static boolean LEADER;

    private final PixelProxy<T, S, P, V> proxy = new PixelProxy<>();

    public PixelProxy<T, S, P, V> process() {
        PixelProxy.rawProxy = this.proxy;
        proxy.getRancher().start();
        return this.proxy;
    }

    public Pixel(JavaPlugin plugin) {
        new StaticTask(plugin);
        Basement.init();
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

    public Pixel<T, S, P, V> registerQueue(V queue) {
        proxy.insertQueue(queue.getType(), queue);
        return this;
    }

}
