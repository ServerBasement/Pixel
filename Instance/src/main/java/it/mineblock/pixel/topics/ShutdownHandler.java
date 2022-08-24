package it.mineblock.pixel.topics;

import it.mineblock.pixel.util.Basement;
import it.thedarksword.basement.api.handler.BasementMessageHandler;
import org.bukkit.Bukkit;

public class ShutdownHandler implements BasementMessageHandler<ShutdownRequest> {

    public ShutdownHandler() {
        Basement.redis().registerTopicListener(ShutdownRequest.TOPIC, this);
    }

    @Override
    public void execute(ShutdownRequest message) {
        if (message.getServer().equals(Basement.get().getServerID()))
            Bukkit.shutdown();
    }

    @Override
    public Class<ShutdownRequest> getCommandClass() {
        return ShutdownRequest.class;
    }

}
