package it.ohalee.pixel.data.leaderboard;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.data.PixelDataHolder;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.Basement;
import it.ohalee.pixel.util.StaticTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.redisson.api.RBucket;

public class LeaderBoardUpdater extends BukkitRunnable {

    private final String prefix;

    public LeaderBoardUpdater(String prefix) {
        this.prefix = prefix;

        StaticTask.runBukkitTaskTimer(this, 20L, 20L * 45, true);
    }

    @Override
    public void run() {
        for (StatsType top : PixelProxy.getStatistics().statsValues()) {
            for (LeaderBoard.Type type : LeaderBoard.Type.values()) {
                PixelDataHolder.leaderBoard(top, type).whenComplete((leaderBoard, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    String leaderboardName = top.placeholder().toLowerCase() + "_" + type.name().toLowerCase();
                    RBucket<LeaderBoard> redisBoard = Basement.rclient().getBucket("pixel_" + prefix + "_" + leaderboardName);
                    redisBoard.set(leaderBoard);
                });
            }
        }
    }


}
