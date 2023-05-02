package it.ohalee.pixel;

import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.api.CrossServerManager;
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

import java.util.UUID;

public abstract class SubPixel<E extends Enum<E> & PixelType, T extends SharedMatch, C extends Match<E, T>> {

    protected static SubPixel<?, ?, ?> raw;
    @Getter
    private final PixelMatchManager<E, T, C> matchManager;
    @Getter
    private final PlayerReceiver<E, T, C> playerReceiver;
    @Getter
    @Setter
    private CrossServerManager crossServerManager;

    public SubPixel(JavaPlugin plugin, CrossServerManager crossServerManager) {
        Basement.init();

        if (Basement.get().redisManager() == null)
            throw new RuntimeException("Redis is not enabled in BasementLib! Can't use pixel without redis!");


        new StaticTask(plugin);
        new ShutdownHandler();
        new StatusHandler();

        matchManager = summonMatchManager();
        playerReceiver = summonPlayerReceiver();
        Bukkit.getPluginManager().registerEvents(playerReceiver, plugin);

        if (crossServerManager != null)
            this.crossServerManager = crossServerManager;
        else {
            if (Basement.get().remoteVelocityService() == null)
                throw new RuntimeException("BasementLib is not enabled in Velocity! Can't use BasementLib-VelocityService without velocity! Please override this method in your implementation!");

            this.crossServerManager = new CrossServerManager() {
                @Override
                public void sendToGameLobby(String username, String lobbyName) {
                    Basement.get().remoteVelocityService().sendToServer(username, lobbyName);
                }

                @Override
                public void sendToServer(String username, String serverID) {
                    Basement.get().remoteVelocityService().sendToServer(username, serverID);
                }

                @Override
                public boolean isOnRanch(UUID uuid) {
                    return Basement.get().remoteVelocityService().isOnRanch(uuid, playerReceiver.lobbyName());
                }
            };
        }

        raw = this;
    }

    public static <SE extends Enum<SE> & PixelType, ST extends SharedMatch, SC extends Match<SE, ST>> SubPixel<SE, ST, SC> getRaw() {
        return (SubPixel<SE, ST, SC>) raw;
    }

    public abstract PixelMatchManager<E, T, C> summonMatchManager();

    public abstract PlayerReceiver<E, T, C> summonPlayerReceiver();

    public void shutdown() {
        Basement.redis().clearTopicListeners(ShutdownRequest.TOPIC);
        Basement.redis().clearTopicListeners(StatusRequest.TOPIC);
        Bukkit.getOnlinePlayers().forEach(p -> SubPixel.getRaw().getCrossServerManager().sendToGameLobby(p.getName(), playerReceiver.lobbyName()));
        matchManager.flush();
        matchManager.clearShared();
    }

}
