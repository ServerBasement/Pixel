package it.ohalee.pixel.api;

public interface CrossServerManager {

    void sendToGameLobby(String username, String lobbyName);

    void sendToServer(String username, String serverID);

}
