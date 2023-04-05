package it.ohalee.pixel.util;

import it.ohalee.basementlib.api.BasementLib;
import it.ohalee.basementlib.api.bukkit.BasementBukkit;
import it.ohalee.basementlib.api.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.redisson.api.RedissonClient;

public class Basement {

    private static final RegisteredServiceProvider<BasementLib> provider = Bukkit.getServicesManager().getRegistration(BasementLib.class);

    private static BasementLib basement;
    private static RedisManager redisManager;

    public static BasementLib get() {
        return basement;
    }

    public static BasementBukkit getBukkit() {
        return (BasementBukkit) basement;
    }

    public static RedisManager redis() {
        return redisManager;
    }

    public static RedissonClient rclient() {
        return redisManager.redissonClient();
    }

    public static void init() {
        if (provider == null) return;
        basement = provider.getProvider();
        redisManager = basement.redisManager();
    }

}
