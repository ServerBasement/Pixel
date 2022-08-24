package it.mineblock.pixel.server;

import it.thedarksword.basement.api.server.BukkitServer;

public abstract class ServerRancherConfiguration {

    abstract String modeName();
    abstract int maxAmountOfServers();
    abstract int maxMatcherPerServer();
    abstract double warningPercentage();

    int minimumIdle() {
        return 2;
    }

    int incremental() {
        return 3;
    }

    InternalServer internalSupplier(int index, BukkitServer server, boolean terminable) {
        return new InternalServer(index, server, terminable);
    }

}
