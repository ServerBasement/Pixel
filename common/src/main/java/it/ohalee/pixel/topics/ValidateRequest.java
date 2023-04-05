package it.ohalee.pixel.topics;

import it.ohalee.basementlib.api.redis.messages.BasementMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequest extends BasementMessage {

    public static final String TOPIC = "pixel_match_validate_request";

    private final String server;
    private final String matchName;

    public ValidateRequest() {
        super(TOPIC);
        server = null;
        matchName = null;
    }

    public ValidateRequest(String server, String matchName) {
        super(TOPIC);
        this.server = server;
        this.matchName = matchName;
    }


}
