package it.bowyard.pixel.topics;

import it.bowyard.pixel.SubPixel;
import it.bowyard.pixel.api.Match;
import it.bowyard.pixel.match.SharedMatchStatus;
import it.bowyard.pixel.util.Basement;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;

public class StatusHandler implements BasementMessageHandler<StatusRequest> {

    public StatusHandler() {
        Basement.redis().registerTopicListener(StatusRequest.TOPIC, this);
    }

    @Override
    public void execute(StatusRequest statusRequest) {
        if (!statusRequest.getServer().equals(Basement.get().getServerID())) return;
        Match<?, ?> match = SubPixel.getRaw().getMatchManager().getMatch(statusRequest.getMatchName());
        if (match == null) return;
        match.warranty(SharedMatchStatus.OPEN);
    }

    @Override
    public Class<StatusRequest> getCommandClass() {
        return StatusRequest.class;
    }

}
