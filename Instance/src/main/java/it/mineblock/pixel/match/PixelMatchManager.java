package it.mineblock.pixel.match;

import it.mineblock.pixel.api.Match;
import it.mineblock.pixel.util.Basement;
import org.bukkit.Bukkit;
import org.redisson.api.RMapCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        match.processFill();
    }

    public void removeMatch(C match) {
        matches.remove(match.getShared().getName());
        match.getJoining().destroy();
        match.getJoining().delete();
        shared.remove(match.getShared().getName());
    }

    public C getMatch(String name) {
        return matches.get(name);
    }

    public void clearShared() {
        shared.clear();
    }

}
