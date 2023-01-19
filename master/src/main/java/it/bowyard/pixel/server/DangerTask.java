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

        double usage;
        try { usage = (double) (rancher.getRunningMatches() / (rancher.getRunningServers() * rancher.MAX_MATCHES_PER_SERVER)) * 100;
        } catch (Exception exp) { return; }
        if (usage >= rancher.WARNING_PERCENTAGE) {
            if (!rancher.startServer(rancher.configuration.incremental())) {
                return;
            }
            System.out.println("WARNING: " + rancher.getRunningServers() + " servers are running " + rancher.getRunningMatches() + " matches. (" + usage + "%)");
            System.out.println("STARTED " + rancher.configuration.incremental() + " SERVERS");
        }
    }

}