package it.ohalee.pixel.server;

import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.hemerald.basementx.api.server.BukkitServer;

public abstract class ServerRancherConfiguration<E extends Enum<E> & PixelType, T extends SharedMatch<E>> {

    public abstract String modeName();

    public abstract int maxAmountOfServers();

    public abstract int maxMatchesPerServer();

    public abstract double warningPercentage();

    public int maxStartOfServerSimultaneously() {
        return incremental();
    }

    public abstract Class<T> sharedMatchClass();

    public int minimumIdle() {
        return 2;
    }

    public int incremental() {
        return 3;
    }

    public InternalServer<E, T> internalSupplier(int index, BukkitServer server, boolean terminable, Class<T> clazz) {
        return new InternalServer<>(index, server, terminable, clazz);
    }

}
