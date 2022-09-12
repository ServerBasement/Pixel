package it.bowyard.pixel.server;

import it.bowyard.pixel.Pixel;
import it.bowyard.pixel.PixelProxy;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.queue.StandardQueue;
import it.bowyard.pixel.topics.ShutdownRequest;
import it.bowyard.pixel.util.Basement;
import it.hemerald.basementx.api.server.BukkitServer;
import lombok.Getter;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryRemovedListener;

public class InternalServer<E extends Enum<E> & PixelType, T extends SharedMatch<E>> {

    @Getter
    private final int index;
    @Getter
    private final BukkitServer server;
    private final boolean terminable;

    private final Class<T> sharedMatchClass;

    @Getter
    private boolean seekable;
    private int stopCount = 0;

    private final RMapCache<String, String> shared;
    private final int listenerId;

    public void addMatch(String matchName, String mapName) {
        shared.fastPut(matchName, mapName);
    }

    public String removeMatch(String matchName) {
        return shared.remove(matchName);
    }

    public int size() {
        return shared.size();
    }

    public InternalServer(int index, BukkitServer server, boolean terminable, Class<T> sharedMatchClass) {
        this.index = index;
        this.server = server;
        this.terminable = terminable;
        this.sharedMatchClass = sharedMatchClass;
        this.shared = Basement.rclient().getMapCache(server.getName() + "_shared");
        if (Pixel.LEADER) {
            listenerId = shared.addListener((EntryRemovedListener<String, String>) event -> dropMatch(event.getKey()));
        } else listenerId = -1;
        seekable = true;
    }

    public void destroy() {
        if (listenerId != -1)
            shared.removeListener(listenerId);
        for (String matchName : shared.keySet()) {
            dropMatch(matchName);
        }
        shared.clear();
        shared.destroy();
    }

    public void dropMatch(String matchName) {
        T match = Basement.rclient().getLiveObjectService().get(sharedMatchClass, matchName);
        StandardQueue<E, T, ?> queue = (StandardQueue<E, T, ?>) PixelProxy.getRawProxy().getQueue(match.getType());
        queue.dropMatch(matchName);
    }

    protected void calledStop() {
        if (!terminable) return;
        if (!shared.isEmpty()) {
            stopCount = Math.max(0, stopCount - shared.size());
            return;
        }
        if (stopCount == 10) {
            seekable = false;
            Basement.redis().publishMessage(new ShutdownRequest(server.getName()));
            return;
        }
        stopCount++;
    }

}
