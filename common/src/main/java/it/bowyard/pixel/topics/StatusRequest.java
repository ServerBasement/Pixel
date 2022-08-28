package it.bowyard.pixel.topics;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest extends BasementMessage {

    public static final String TOPIC = "pixel_match_status_request";

    private final String server;
    private final String matchName;

    public StatusRequest() {
        super(TOPIC);
        server = null;
        matchName = null;
    }

    public StatusRequest(String server, String matchName) {
        this.server = server;
        this.matchName = matchName;
    }


}
