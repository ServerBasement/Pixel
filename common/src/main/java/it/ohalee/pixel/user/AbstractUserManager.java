package it.ohalee.pixel.user;

import it.ohalee.pixel.api.PixelUser;
import it.ohalee.pixel.api.UserManager;
import it.ohalee.pixel.api.UserNotFoundException;
import it.ohalee.pixel.stats.Statistics;
import it.ohalee.pixel.util.Basement;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractUserManager<T, K, U extends PixelUser<K>> implements UserManager<T, U, K> {

    protected final Map<K, U> loadedUsers = new ConcurrentHashMap<>();
    protected final boolean statsEnabled;

    public AbstractUserManager(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }

    protected abstract U createUser(T type);

    @Override
    public U user(K key) throws UserNotFoundException {
        U user = loadedUsers.get(key);
        if (user == null) throw new UserNotFoundException("No user found for " + key);
        return user;
    }

    @Override
    public CompletableFuture<U> load(T type, K key) throws UserNotFoundException {
        return CompletableFuture.supplyAsync(() -> {
            U user = createUser(type);
            if (statsEnabled) {
                user.assign(stats(key).join());
            }
            loadedUsers.put(user.key(), user);
            return user;
        });
    }

    @Override
    public CompletableFuture<Statistics> stats(K key) {
        return CompletableFuture.completedFuture(Basement.get().redisManager().redissonClient().getLiveObjectService().get(Statistics.class, key));
    }

    @Override
    public CompletableFuture<Void> unload(U user) {
        loadedUsers.remove(user.key());
        return CompletableFuture.completedFuture(null);
    }

}
