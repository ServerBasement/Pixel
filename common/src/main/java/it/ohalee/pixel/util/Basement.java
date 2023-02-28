package it.ohalee.pixel.util;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.redisson.api.RedissonClient;

public class Basement {

    private static final RegisteredServiceProvider<BasementBukkit> provider = Bukkit.getServicesManager().getRegistration(BasementBukkit.class);

    private static BasementBukkit basement;
    private static RedisManager redisManager;

    public static BasementBukkit get() {
        return basement;
    }

    public static RedisManager redis() {
        return redisManager;
    }

    public static RedissonClient rclient() {
        return redisManager.getRedissonClient();
    }

    public static void init() {
        if (provider == null) return;
        basement = provider.getProvider();
        redisManager = basement.getRedisManager();
    }

}
