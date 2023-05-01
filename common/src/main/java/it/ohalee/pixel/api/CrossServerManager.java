package it.ohalee.pixel.api;

import java.util.UUID;

public interface CrossServerManager {

    void sendToGameLobby(String username, String lobbyName);

    void sendToServer(String username, String serverID);

    boolean isOnRanch(UUID uuid);

}
