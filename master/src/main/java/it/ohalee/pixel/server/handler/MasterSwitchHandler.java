package it.ohalee.pixel.server.handler;

import it.ohalee.basementlib.api.redis.messages.handler.BasementMessageHandler;
import it.ohalee.pixel.Pixel;
import it.ohalee.pixel.util.Basement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MasterSwitchHandler implements BasementMessageHandler<MasterSwitchMessage> {

    private final String modeName;

    @Override
    public void execute(MasterSwitchMessage masterSwitchMessage) {
        if (!modeName.equalsIgnoreCase(masterSwitchMessage.getMode()) && !masterSwitchMessage.getNewLeader().equalsIgnoreCase(Basement.getBukkit().getServerID()))
            return;
        Pixel.LOGGER.info("This server is now mastering mode " + modeName);
        Pixel.setLEADER(true);
    }

    @Override
    public Class<MasterSwitchMessage> getCommandClass() {
        return MasterSwitchMessage.class;
    }
}
