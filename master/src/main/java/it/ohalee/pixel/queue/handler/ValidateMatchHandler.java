package it.ohalee.pixel.queue.handler;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.server.InternalServer;
import it.ohalee.pixel.topics.ValidateRequest;
import it.ohalee.basementlib.api.redis.messages.handler.BasementMessageHandler;

public class ValidateMatchHandler implements BasementMessageHandler<ValidateRequest> {

    @Override
    public void execute(ValidateRequest validateRequest) {
        InternalServer<?, ?> server = PixelProxy.getRawProxy().getRancher().getServer(validateRequest.getServer());
        if (server == null) return;
        server.validateMatch(validateRequest.getMatchName());
    }

    @Override
    public Class<ValidateRequest> getCommandClass() {
        return ValidateRequest.class;
    }

}
