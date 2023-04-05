package it.ohalee.pixel;

import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.api.PlayerManager;
import it.ohalee.pixel.match.PixelMatchManager;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.player.PlayerReceiver;
import it.ohalee.pixel.topics.ShutdownHandler;
import it.ohalee.pixel.topics.ShutdownRequest;
import it.ohalee.pixel.topics.StatusHandler;
import it.ohalee.pixel.topics.StatusRequest;
import it.ohalee.pixel.util.Basement;
import it.ohalee.pixel.util.StaticTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SubPixel<E extends Enum<E> & PixelType, T extends SharedMatch<E>, C extends Match<E, T>> {

    protected static SubPixel<?, ?, ?> raw;
    @Getter
    private final PixelMatchManager<E, T, C> matchManager;
    @Getter
    private final PlayerReceiver<E, T, C> playerReceiver;
    @Getter
    @Setter
    private PlayerManager playerManager;

    public SubPixel(JavaPlugin plugin, PlayerManager playerManager) {
        Basement.init();

        if (Basement.get().redisManager() == null)
            throw new RuntimeException("Redis is not enabled in BasementLib! Can't use pixel without redis!");

        if (playerManager != null)
            this.playerManager = playerManager;
        else {
            if (Basement.get().remoteVelocityService() == null)
                throw new RuntimeException("BasementLib is not enabled in Velocity! Can't use BasementLib-VelocityService without velocity!");

            this.playerManager = new PlayerManager() {
                @Override
                public void sendToGameLobby(String username, String lobbyName) {
                    Basement.get().remoteVelocityService().sendToServer(username, lobbyName);
                }

                @Override
                public void sendToServer(String username, String serverID) {
                    Basement.get().remoteVelocityService().sendToServer(username, serverID);
                }
            };
        }

        new StaticTask(plugin);
        new ShutdownHandler();
        new StatusHandler();
        matchManager = summonMatchManager();
        playerReceiver = summonPlayerReceiver();
        Bukkit.getPluginManager().registerEvents(playerReceiver, plugin);
        raw = this;
    }

    public static <SE extends Enum<SE> & PixelType, ST extends SharedMatch<SE>, SC extends Match<SE, ST>> SubPixel<SE, ST, SC> getRaw() {
        return (SubPixel<SE, ST, SC>) raw;
    }

    public abstract PixelMatchManager<E, T, C> summonMatchManager();

    public abstract PlayerReceiver<E, T, C> summonPlayerReceiver();

    public void shutdown() {
        Basement.redis().clearTopicListeners(ShutdownRequest.TOPIC);
        Basement.redis().clearTopicListeners(StatusRequest.TOPIC);
        Bukkit.getOnlinePlayers().forEach(p -> SubPixel.getRaw().getPlayerManager().sendToGameLobby(p.getName(), playerReceiver.lobbyName()));
        matchManager.flush();
        matchManager.clearShared();
    }

}
