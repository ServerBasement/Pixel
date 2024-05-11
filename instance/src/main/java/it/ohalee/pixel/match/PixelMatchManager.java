package it.ohalee.pixel.match;

import it.ohalee.pixel.SubPixel;
import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.util.Basement;
import it.ohalee.pixel.util.StaticTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.redisson.api.RMapCache;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class PixelMatchManager<E extends Enum<E> & PixelType, T extends SharedMatch, C extends Match<E, T>> {

    private static RMapCache<String, String> shared;
    private final Map<String, C> matches = new HashMap<>();

    public PixelMatchManager(IncomingSharedListener<E, T, C> listener) {
        shared = Basement.rclient().getMapCache(Basement.getBukkit().getServerID() + "_shared");
        shared.addListener(listener);
    }

    public List<C> getMatches() {
        return new ArrayList<>(matches.values());
    }

    public void putMatch(C match) {
        matches.put(match.getShared().getName(), match);
    }

    public void flush() {
        for (C match : new HashSet<>(matches.values())) {
            removeMatch(match);
        }
    }

    public abstract Class<E> typeClass();

    public abstract String getLobby();

    public void removeMatch(C match) {
        match.getJoining().destroy();
        match.getJoining().delete();
        match.getShared_spectators().destroy();
        match.getShared_spectators().delete();
        matches.remove(match.getShared().getName());
        shared.remove(match.getShared().getName());

        World world = Bukkit.getWorld(match.getWorldName());

        CompletableFuture.runAsync(() -> {
            for (Player player : world.getPlayers())
                SubPixel.getRaw().getCrossServerManager().sendToGameLobby(player.getName(), getLobby());
        }).whenComplete((voidValue, error) ->
                StaticTask.runBukkitTaskTimer(new WorldRemoverTask(postWorldUnload(match), world), 0L, 10L, false));
    }

    protected Runnable postWorldUnload(C match) {
        return () -> {};
    }

    public C getMatch(String name) {
        return matches.get(name);
    }

    public void clearShared() {
        shared.clear();
    }

}
