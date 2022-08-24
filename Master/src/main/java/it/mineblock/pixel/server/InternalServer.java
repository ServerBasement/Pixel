package it.mineblock.pixel.server;

import it.mineblock.pixel.Pixel;
import it.mineblock.pixel.PixelProxy;
import it.mineblock.pixel.match.SharedMatch;
import it.mineblock.pixel.queue.StandardQueue;
import it.mineblock.pixel.topics.ShutdownRequest;
import it.mineblock.pixel.util.Basement;
import it.thedarksword.basement.api.server.BukkitServer;
import lombok.Getter;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryRemovedListener;

public class InternalServer {

    @Getter
    private final int index;
    @Getter
    private final BukkitServer server;
    private final boolean terminable;

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

    public InternalServer(int index, BukkitServer server, boolean terminable) {
        this.index = index;
        this.server = server;
        this.terminable = terminable;
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
        SharedMatch<?> match = Basement.rclient().getLiveObjectService().get(SharedMatch.class, matchName);
        StandardQueue<?, ?, ?> queue = (StandardQueue<?, ?, ?>) PixelProxy.getRawProxy().getQueue(match.getType());
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
