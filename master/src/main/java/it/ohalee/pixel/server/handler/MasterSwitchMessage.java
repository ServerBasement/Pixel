package it.ohalee.pixel.server.handler;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class MasterSwitchMessage extends BasementMessage {

    public final static String TOPIC = "master_switch";

    private final String mode;
    private final String newLeader;

    public MasterSwitchMessage() {
        super(TOPIC);
        this.mode = null;
        this.newLeader = null;
    }

    public MasterSwitchMessage(String mode, String newLeader) {
        super(TOPIC);
        this.mode = mode;
        this.newLeader = newLeader;
    }

}
