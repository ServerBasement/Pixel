package it.bowyard.pixel.server;

import it.bowyard.pixel.Pixel;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class DangerTask extends BukkitRunnable {

    private final ServerRancher<?, ?> rancher;
    @Override
    public void run() {
        if (!Pixel.LEADER) return;
        rancher.internalServers.values().forEach(InternalServer::calledStop);
        rancher.requestedServers.values().removeIf(value -> value-System.currentTimeMillis() > 60000);

        double usage;
        try { usage = (double) (rancher.getRunningMatches() / ((rancher.getRunningServers()+rancher.requestedServers.size()) * rancher.MAX_MATCHES_PER_SERVER)) * 100;
        } catch (Exception exp) { return; }
        if (usage >= rancher.WARNING_PERCENTAGE)
            rancher.startServer(rancher.configuration.incremental());
    }

}