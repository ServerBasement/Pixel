package it.mineblock.pixel.player;

import it.mineblock.pixel.SubPixel;
import it.mineblock.pixel.api.Match;
import it.mineblock.pixel.match.PixelType;
import it.mineblock.pixel.match.SharedMatch;
import it.mineblock.pixel.util.Basement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerReceiver<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> implements Listener {

    private final static Map<String, String> joining = new HashMap<>();

    public static void addJoining(String username, String matchname) {
        joining.put(username, matchname);
        Basement.get().getPlayerManager().sendToServer(username, Basement.get().getServerID());
    }

    public static void removeJoining(String username) {
        joining.remove(username);
    }

    abstract String bypassPermission();
    abstract String lobbyName();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        String matchName = joining.get(player.getName());
        if (matchName == null) {
            if (player.hasPermission(bypassPermission())) return;
            Basement.get().getPlayerManager().sendToGameLobby(player.getName(), lobbyName());
            return;
        }
        C match = SubPixel.<E, T, C>getRaw().getMatchManager().getMatch(matchName);
        match.letJoin(player);
        joining.remove(player.getName());
    }

    @EventHandler
    abstract void onQuit(PlayerQuitEvent event);

    @EventHandler
    abstract void onLeave(PlayerKickEvent event);

}
