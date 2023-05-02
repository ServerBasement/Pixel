package it.ohalee.pixel.data.leaderboard;

import it.ohalee.basementlib.api.persistence.maria.queries.builders.WhereBuilder;
import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.data.PixelDataHolder;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.StaticTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

public class LeaderBoardReset extends BukkitRunnable {

    private ZonedDateTime time = ZonedDateTime.now();

    public LeaderBoardReset() {
        StaticTask.runBukkitTaskTimer(this, 20L * 60, 20L * 60, true);
    }

    @Override
    public void run() {
        ZonedDateTime now = ZonedDateTime.now();
        if (now.getDayOfMonth() != time.getDayOfMonth()) {
            for (LeaderBoard.Type type : LeaderBoard.Type.values()) {
                if (type.time() == null) continue;
                if (type == LeaderBoard.Type.WEEKLY && now.getDayOfWeek() != DayOfWeek.MONDAY) continue;

                QueryBuilderUpdate queryBuilderUpdate = PixelDataHolder.LEADERBOARD_UPDATE.patternClone();

                for (StatsType t : PixelProxy.getStatistics().statsValues())
                    if (t.resettable())
                        queryBuilderUpdate.set(t.dbColumn(), 0);

                queryBuilderUpdate.where(WhereBuilder.builder().equalsNQ("temporal", type.id()).close()).build().exec();
                System.out.println("Leaderboard " + type.name() + " resetted");
            }

            time = now;
        }
    }
}
