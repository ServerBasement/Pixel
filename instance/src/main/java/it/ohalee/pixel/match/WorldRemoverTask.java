package it.ohalee.pixel.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class WorldRemoverTask extends BukkitRunnable {

    private final Runnable postWorldUnload;
    private final World world;

    @Override
    public void run() {
        if (!world.getPlayers().isEmpty()) return;
        Bukkit.unloadWorld(world, false);
        postWorldUnload.run();
        cancel();
    }

}
