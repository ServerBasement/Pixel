package it.ohalee.pixel.player;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.api.CrossServerManager;
import it.ohalee.pixel.user.AbstractUserManager;
import it.ohalee.pixel.util.Basement;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PixelParticipatorManager extends AbstractUserManager<Player, UUID, PixelParticipator> implements CrossServerManager {

    private final String ranch;

    public PixelParticipatorManager(String ranch, boolean statsEnabled) {
        super(statsEnabled);
        this.ranch = ranch;
    }

    public static PixelParticipator get(UUID uuid) {
        return PixelProxy.getRawProxy().getPlayerManager().user(uuid);
    }

    @Override
    protected PixelParticipator createUser(Player player) {
        return new PixelParticipator(player);
    }

    @Override
    public void sendToGameLobby(String username, String lobbyName) {}

    @Override
    public void sendToServer(String username, String serverID) {}

    @Override
    public boolean isOnRanch(UUID uuid) {
        if (Basement.get().remoteVelocityService() == null)
            throw new RuntimeException("BasementLib is not enabled in Velocity! Can't use BasementLib-VelocityService without velocity! Please override this method in your implementation!");
        return Basement.get().remoteVelocityService().isOnRanch(uuid, ranch);
    }

}
