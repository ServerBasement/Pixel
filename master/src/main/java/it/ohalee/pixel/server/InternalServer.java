package it.ohalee.pixel.server;

import it.ohalee.pixel.Pixel;
import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.queue.StandardQueue;
import it.ohalee.pixel.topics.ShutdownRequest;
import it.ohalee.pixel.util.Basement;
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

    private final long startedAt = System.currentTimeMillis();
    private final RMapCache<String, String> shared;
    private final int listenerId;
    @Getter
    private boolean seekable;
    private int stopCount = 0;
    private boolean loadingMatch = false;

    public InternalServer(int index, BukkitServer server, boolean terminable, Class<T> sharedMatchClass) {
        this.index = index;
        this.server = server;
        this.terminable = terminable;
        this.sharedMatchClass = sharedMatchClass;
        this.shared = Basement.rclient().getMapCache(server.getName() + "_shared");
        listenerId = shared.addListener((EntryRemovedListener<String, String>) event -> {
            if (!Pixel.LEADER) return;
            dropMatch(event.getKey());
        });
        seekable = true;
    }

    public void loadingMatch(boolean loadingMatch) {
        this.loadingMatch = loadingMatch;
    }

    public boolean isLoadingMatch() {
        return loadingMatch;
    }

    public void addMatch(String matchName, String mapName) {
        shared.fastPut(matchName, mapName);
    }

    public String removeMatch(String matchName) {
        return shared.remove(matchName);
    }

    public int size() {
        return loadingMatch ? shared.size() + 1 : shared.size();
    }

    public void validateMatch(String matchName) {
        T match = Basement.rclient().getLiveObjectService().get(sharedMatchClass, matchName);
        StandardQueue<E, T, ?> queue = (StandardQueue<E, T, ?>) PixelProxy.getRawProxy().getQueue(E.valueOf(match.typeClass(), match.getType()));
        queue.validateMatch(match);
        loadingMatch(false);
    }

    public void destroy() {
        shared.removeListener(listenerId);
        for (String matchName : shared.keySet()) {
            dropMatch(matchName);
        }
        shared.clear();
        shared.destroy();
    }

    public void dropMatch(String matchName) {
        T match = Basement.rclient().getLiveObjectService().get(sharedMatchClass, matchName);
        StandardQueue<E, T, ?> queue = (StandardQueue<E, T, ?>) PixelProxy.getRawProxy().getQueue(E.valueOf(match.typeClass(), match.getType()));
        queue.dropMatch(matchName);
    }

    protected void calledStop() {
        if (!terminable || (System.currentTimeMillis() - startedAt < 300000)) return;
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
