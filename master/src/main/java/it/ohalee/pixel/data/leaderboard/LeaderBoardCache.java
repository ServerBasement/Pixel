package it.ohalee.pixel.data.leaderboard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class LeaderBoardCache {

    private static final Cache<String, LeaderBoard> LEADERBOARD_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public static void put(String key, LeaderBoard leaderBoard) {
        LEADERBOARD_CACHE.put(key, leaderBoard);
    }

    public static LeaderBoard get(String key) {
        return LEADERBOARD_CACHE.getIfPresent(key);
    }

}
