package it.mineblock.pixel;

import it.mineblock.pixel.api.Queue;
import it.mineblock.pixel.match.PixelType;
import it.mineblock.pixel.match.SharedMatch;
import it.mineblock.pixel.api.Participator;
import it.mineblock.pixel.server.ServerRancher;
import lombok.Getter;

import java.util.HashMap;

public class PixelProxy<T extends Enum<T> & PixelType, P extends Participator, V extends Queue<T, ? extends SharedMatch<T>, P>> {

    @Getter
    protected static PixelProxy rawProxy;

    @Getter
    private ServerRancher rancher;

    private final HashMap<T, V> queues = new HashMap<>();

    public V getQueue(T key) {
        return queues.get(key);
    }

    protected void insertQueue(T key, V queue) {
        queue.setRancher(rancher);
        queues.put(key, queue);
    }

    protected void setRancher(ServerRancher rancher) {
        this.rancher = rancher;
    }

}
