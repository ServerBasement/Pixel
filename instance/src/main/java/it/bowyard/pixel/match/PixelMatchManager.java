package it.bowyard.pixel.match;

import it.bowyard.pixel.api.Match;
import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.util.StaticTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.redisson.api.RMapCache;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class PixelMatchManager<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> {

    private final Map<String, C> matches = new HashMap<>();
    private static RMapCache<String, String> shared;

    public PixelMatchManager(IncomingSharedListener<E, T, C> listener) {
        shared = Basement.rclient().getMapCache(Basement.get().getServerID() + "_shared");
        shared.addListener(listener);
    }

    public List<C> getMatches() {
        return new ArrayList<>(matches.values());
    }

    public void putMatch(C match) {
        matches.put(match.getShared().getName(), match);

        System.out.println("Match added: " + match.getShared().getName());
    }

    public void flush() {
        System.out.println("Flushing...");
        Iterator<C> iterator = matches.values().iterator();
        while (iterator.hasNext()) {
            C match = iterator.next();
            removeMatch(match);
        }
    }

    public abstract String getLobby();

    public void removeMatch(C match) {
        match.getJoining().destroy();
        match.getJoining().delete();
        match.getShared_spectators().destroy();
        match.getShared_spectators().delete();
        matches.remove(match.getShared().getName());
        shared.remove(match.getShared().getName());

        System.out.println("Match removed: " + match.getShared().getName());

        World world = Bukkit.getWorld(match.getWorldName());

        CompletableFuture.runAsync(() -> {
            for (Player player : world.getPlayers())
                Basement.get().getPlayerManager().sendToGameLobby(player.getName(), getLobby());
        }).whenComplete((voidValue, error) ->
                StaticTask.runBukkitTaskTimer(new WorldRemoverTask(world), 0L, 10L, false));
    }

    public C getMatch(String name) {
        return matches.get(name);
    }

    public void clearShared() {
        shared.clear();
    }

}
