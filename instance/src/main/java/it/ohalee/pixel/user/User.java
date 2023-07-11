package it.ohalee.pixel.user;

import it.ohalee.pixel.api.PixelUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class User implements PixelUser<UUID> {

    private final UUID uniqueId;

    @Override
    public UUID key() {
        return uniqueId;
    }

}
