package it.ohalee.pixel.player;

import it.ohalee.pixel.SubPixel;
import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.util.Basement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerReceiver<E extends Enum<E> & PixelType, T extends SharedMatch, C extends Match<E, T>> implements Listener {

    protected final static Map<String, String> joining = new HashMap<>();

    public static void addJoining(String username, String matchname) {
        joining.put(username, matchname);
        SubPixel.getRaw().getCrossServerManager().sendToServer(username, Basement.getBukkit().getServerID());
    }

    public static void removeJoining(String username) {
        joining.remove(username);
    }

    public abstract String bypassPermission();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        String matchName = joining.get(player.getName());
        if (matchName == null) {
            if (player.hasPermission(bypassPermission())) return;
            SubPixel.getRaw().getCrossServerManager().sendToGameLobby(player.getName(), SubPixel.getRaw().getMatchManager().getLobby());
            return;
        }
        C match = SubPixel.<E, T, C>getRaw().getMatchManager().getMatch(matchName);
        match.letJoin(player);
        joining.remove(player.getName());
    }

    @EventHandler
    public abstract void onQuit(PlayerQuitEvent event);

    @EventHandler
    public abstract void onLeave(PlayerKickEvent event);

}
