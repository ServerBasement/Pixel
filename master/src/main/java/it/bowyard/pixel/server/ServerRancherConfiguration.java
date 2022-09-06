package it.bowyard.pixel.server;

import it.hemerald.basementx.api.server.BukkitServer;

public abstract class ServerRancherConfiguration {

    public abstract String modeName();
    public abstract int maxAmountOfServers();
    public abstract int maxMatchesPerServer();
    public abstract double warningPercentage();

    public int minimumIdle() {
        return 2;
    }

    public int incremental() {
        return 3;
    }

    public InternalServer internalSupplier(int index, BukkitServer server, boolean terminable) {
        return new InternalServer(index, server, terminable);
    }

}
