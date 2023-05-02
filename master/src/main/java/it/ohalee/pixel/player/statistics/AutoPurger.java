package it.ohalee.pixel.player.statistics;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.stats.Statistics;
import it.ohalee.pixel.util.Basement;
import it.ohalee.pixel.util.StaticTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.redisson.api.RLiveObjectService;

import java.util.UUID;

public class AutoPurger extends BukkitRunnable {

    private final static long TIME_SEEN = 3600000;

    private static RLiveObjectService service;

    public AutoPurger() {
        service = Basement.rclient().getLiveObjectService();
        StaticTask.runBukkitTaskTimer(this, 20L * 62 * 4, 20L * 62 * 4, true);
    }

    @Override
    public void run() {
        Iterable<String> statisticsIterable = service.findIds(Statistics.class);
        long start = System.currentTimeMillis();
        int scanned = 0;
        int deletedUsers = 0;

        for (String uuid : statisticsIterable) {
            scanned++;
            Statistics stats = service.get(Statistics.class, uuid);
            if (checkTime(stats)) deletedUsers++;
        }
        System.out.println("[AutoPurger] Done in " + (System.currentTimeMillis() - start) + " ms.." + " Purged " + deletedUsers + ", Total scan on " + scanned + " statistics.");
    }

    private boolean checkTime(Statistics statistics) {
        if (statistics == null) return true;
        long now = System.currentTimeMillis();
        if (now < statistics.lastTimeSeen() + TIME_SEEN) return false;
        if (statistics.playing() || PixelProxy.getRawProxy().getPlayerManager().isOnRanch(UUID.fromString(statistics.uuid()))) {
            statistics.lastTimeSeen(now);
            return false;
        }
        service.delete(statistics);
        return true;
    }

}
