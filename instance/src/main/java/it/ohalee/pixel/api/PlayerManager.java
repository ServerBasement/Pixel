package it.ohalee.pixel.api;

public interface PlayerManager {

    void sendToGameLobby(String username, String lobbyName);

    void sendToServer(String username, String serverID);

}
