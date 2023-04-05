package it.ohalee.pixel.topics;

import it.ohalee.basementlib.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class ShutdownRequest extends BasementMessage {
    public static final String TOPIC = "server_instance_shutdown";

    private final String server;

    public ShutdownRequest() {
        super(TOPIC);
        server = null;
    }

    public ShutdownRequest(String server) {
        super(TOPIC);
        this.server = server;
    }
}
