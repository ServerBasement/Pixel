package it.ohalee.pixel.topics;

import it.ohalee.pixel.util.Basement;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

@Slf4j(topic = "pixel")
public class ShutdownHandler implements BasementMessageHandler<ShutdownRequest> {

    public ShutdownHandler() {
        Basement.redis().registerTopicListener(ShutdownRequest.TOPIC, this);
    }

    @Override
    public void execute(ShutdownRequest message) {
        if (message.getServer().equals(Basement.get().getServerID())) {
            log.info("ShutdownRequest received by Pixel");
            Bukkit.shutdown();
        }
    }

    @Override
    public Class<ShutdownRequest> getCommandClass() {
        return ShutdownRequest.class;
    }

}
