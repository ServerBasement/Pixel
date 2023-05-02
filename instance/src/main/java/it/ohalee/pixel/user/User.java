package it.ohalee.pixel.user;

import it.ohalee.pixel.api.PixelUser;
import it.ohalee.pixel.stats.Statistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class User implements PixelUser<UUID> {

    private final UUID uniqueId;
    private @Nullable Statistics statistics;

    @Override
    public UUID key() {
        return uniqueId;
    }

    @Override
    public void assign(Statistics statistics) {
        this.statistics = statistics;
    }

}
