package it.ohalee.pixel.player;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.api.CrossServerManager;
import it.ohalee.pixel.api.UserNotFoundException;
import it.ohalee.pixel.user.AbstractUserManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PixelParticipatorManager extends AbstractUserManager<Player, UUID, PixelParticipator> implements CrossServerManager {

    private final String ranch;

    public PixelParticipatorManager(String ranch) {
        this.ranch = ranch;
    }

    public static PixelParticipator get(UUID uuid) {
        try {
            return PixelProxy.getRawProxy().getPlayerManager().user(uuid);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PixelParticipator createUser(Player player) {
        return new PixelParticipator(player);
    }

    @Override
    public void sendToGameLobby(String username, String lobbyName) {
    }

    @Override
    public void sendToServer(String username, String serverID) {
    }

}
