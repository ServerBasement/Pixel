package it.ohalee.pixel.topics;

import it.ohalee.basementlib.api.redis.messages.handler.BasementMessageHandler;
import it.ohalee.pixel.SubPixel;
import it.ohalee.pixel.api.Match;
import it.ohalee.pixel.match.SharedMatchStatus;
import it.ohalee.pixel.util.Basement;

public class StatusHandler implements BasementMessageHandler<StatusRequest> {

    public StatusHandler() {
        Basement.redis().registerTopicListener(StatusRequest.TOPIC, this);
    }

    @Override
    public void execute(StatusRequest statusRequest) {
        if (!statusRequest.getServer().equals(Basement.getBukkit().getServerID())) return;
        Match<?, ?> match = SubPixel.getRaw().getMatchManager().getMatch(statusRequest.getMatchName());
        if (match == null) return;
        match.warranty(SharedMatchStatus.OPEN);
    }

    @Override
    public Class<StatusRequest> getCommandClass() {
        return StatusRequest.class;
    }

}
