package it.bowyard.pixel.server;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class DangerTask extends BukkitRunnable {

    private final ServerRancher<?, ?> rancher;
    @Override
    public void run() {
        rancher.internalServers.values().forEach(InternalServer::calledStop);

        double usage;
        try { usage = (double) (rancher.getRunningMatches() / (rancher.getRunningServers() * rancher.MAX_MATCHES_PER_SERVER)) * 100;
        } catch (Exception exp) { return; }
        if (usage >= rancher.WARNING_PERCENTAGE)
            rancher.startServer(rancher.configuration.incremental());
    }

}