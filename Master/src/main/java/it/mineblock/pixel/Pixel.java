package it.mineblock.pixel;

import it.mineblock.pixel.api.Queue;
import it.mineblock.pixel.match.PixelType;
import it.mineblock.pixel.match.SharedMatch;
import it.mineblock.pixel.api.Participator;
import it.mineblock.pixel.server.ServerRancher;
import it.mineblock.pixel.server.ServerRancherConfiguration;
import it.mineblock.pixel.util.Basement;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class Pixel <T extends Enum<T> & PixelType, P extends Participator, V extends Queue<T, ? extends SharedMatch<T>, P>>  {

    public static Logger LOGGER;
    public static boolean LEADER;

    private final PixelProxy<T, P, V> proxy = new PixelProxy<>();

    public PixelProxy<T, P, V> process() {
        PixelProxy.rawProxy = this.proxy;
        proxy.getRancher().start();
        return this.proxy;
    }

    public Pixel() {
        Basement.init();
    }

    public Pixel<T, P, V> metadata(boolean isLeader) {
        LEADER = isLeader;
        return this;
    }

    public Pixel<T, P, V> registerLogger(Logger logger) {
        LOGGER = logger;
        return this;
    }

    public Pixel<T, P, V> registerRancher(JavaPlugin plugin, ServerRancherConfiguration configuration) {
        proxy.setRancher(new ServerRancher(plugin, configuration));
        return this;
    }

    public Pixel<T, P, V> registerQueue(V queue) {
        proxy.insertQueue(queue.getType(), queue);
        return this;
    }

}
