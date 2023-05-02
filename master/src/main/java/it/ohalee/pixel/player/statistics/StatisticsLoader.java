package it.ohalee.pixel.player.statistics;

import it.ohalee.basementlib.api.persistence.maria.queries.builders.WhereBuilder;
import it.ohalee.basementlib.api.persistence.maria.structure.data.QueryData;
import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.data.PixelDataHolder;
import it.ohalee.pixel.player.PixelParticipator;
import it.ohalee.pixel.player.PixelParticipatorManager;
import it.ohalee.pixel.stats.Statistics;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.Basement;
import org.bukkit.entity.Player;
import org.redisson.api.RLiveObjectService;

import java.util.concurrent.CompletableFuture;

public class StatisticsLoader {

    private static RLiveObjectService liveService;

    public StatisticsLoader() {
        liveService = Basement.rclient().getLiveObjectService();
    }

    public static CompletableFuture<Statistics> loadFromDatabase(String uuid) {
        return PixelDataHolder.DEFAULT_SELECT_ID.patternClone()
                .where(WhereBuilder.builder().equals("uuid", uuid).close())
                .build().execReturnAsync().thenApplyAsync(queryData -> {
                    int primaryKeyData = queryData.getInt("id");

                    QueryData data = PixelDataHolder.DEFAULT_SELECT_USER.patternClone()
                            .where(WhereBuilder.builder().equalsNQ("user_id", primaryKeyData).close())
                            .build().execReturn();

                    Statistics statistics = new Statistics(uuid, primaryKeyData);
                    if (!data.isBeforeFirst()) { // if user does not exist on bw tables
                        statistics.exists(false);
                        return statistics;
                    }

                    statistics.exists(true);

                    if (data.next()) {
                        for (StatsType type : PixelProxy.getStatistics().statsValues()) {
                            statistics.write(type, data.getInt(type.dbColumn()));
                        }
                    }
                    return statistics;
                });
    }

    public static CompletableFuture<Statistics> get(Player player) {
        // TRY FROM 'ONLINE'
        PixelParticipator tryParticipator = PixelParticipatorManager.get(player.getUniqueId());
        if (tryParticipator != null && tryParticipator.statistics() != null) {
            tryParticipator.statistics().lastTimeSeen(System.currentTimeMillis());
            return CompletableFuture.completedFuture(tryParticipator.statistics());
        }

        // TRY FROM 'LOADED ON REDIS'
        Statistics stats = liveService.get(Statistics.class, player.getUniqueId().toString());
        if (stats != null) { // stats loaded and up to date
            stats.lastTimeSeen(System.currentTimeMillis());
            return CompletableFuture.completedFuture(stats);
        }

        // LOAD DATA FROM DATABASE
        return loadFromDatabase(player.getUniqueId().toString()).thenApplyAsync((plain) -> {
            plain.lastTimeSeen(System.currentTimeMillis());
            return liveService.merge(plain);
        });
    }
}
