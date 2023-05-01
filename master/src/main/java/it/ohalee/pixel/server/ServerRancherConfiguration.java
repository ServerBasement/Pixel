package it.ohalee.pixel.server;

import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.basementlib.api.server.BukkitServer;
import it.ohalee.pixel.util.Basement;

public abstract class ServerRancherConfiguration<E extends Enum<E> & PixelType, T extends SharedMatch> {

    public abstract String modeName();

    public abstract int maxMatchesPerServer();

    public abstract double warningPercentage();

    public abstract Class<T> sharedMatchClass();

    public abstract Class<E> typeClass();

    public abstract ServerManagerConfiguration<E, T> serverManager();

    public static abstract class ServerManagerConfiguration<E extends Enum<E> & PixelType, T extends SharedMatch> {

        public abstract boolean dynamicallyStartServers();

        public DynamicServerManager dynamicServerManager() {
            return new DynamicServerManager() {
                @Override
                public void startServer(String name) {
                    if (Basement.get().remoteCerebrumService() == null)
                        throw new IllegalStateException("BasementLib Remote cerebrum service is not available");

                    Basement.get().remoteCerebrumService().createServer(name);
                }
            };
        }

        public abstract int maxAmountOfServers();

        public int minimumIdle() {
            return 5;
        }

        public int incremental() {
            return 3;
        }

        public int maxStartOfServerSimultaneously() {
            return incremental();
        }

        public InternalServer<E, T> internalSupplier(int index, BukkitServer server, boolean terminable, Class<T> clazz) {
            return new InternalServer<>(index, server, dynamicallyStartServers() && terminable, clazz);
        }

        public static abstract class DynamicServerManager {

            public abstract void startServer(String name);

        }

    }

}
