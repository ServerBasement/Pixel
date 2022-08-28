package it.bowyard.pixel.topics;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class ShutdownRequest extends BasementMessage {
    public static final String TOPIC = "server_instance_shutdown";

    private final String server;

    public ShutdownRequest() {
        server = null;
    }

    public ShutdownRequest(String server) {
        this.server = server;
    }
}
