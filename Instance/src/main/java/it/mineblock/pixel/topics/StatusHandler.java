package it.mineblock.pixel.topics;

import it.mineblock.pixel.SubPixel;
import it.mineblock.pixel.api.Match;
import it.mineblock.pixel.match.SharedMatchStatus;
import it.mineblock.pixel.util.Basement;
import it.thedarksword.basement.api.handler.BasementMessageHandler;

public class StatusHandler implements BasementMessageHandler<StatusRequest> {

    private void post() {
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
