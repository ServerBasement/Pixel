package it.ohalee.pixel.server.statistics;

import it.ohalee.pixel.Pixel;
import it.ohalee.pixel.data.PixelDataHolder;
import it.ohalee.pixel.placeholder.LobbyPlaceholders;
import it.ohalee.pixel.player.statistics.AutoPurger;
import it.ohalee.pixel.player.statistics.StatisticsLoader;
import it.ohalee.pixel.stats.StatsType;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PixelStatistics<T extends Enum<T> & StatsType> {

    private final ServerStatsConfiguration<T> configuration;

    public PixelStatistics(ServerStatsConfiguration<T> configuration) {
        this.configuration = configuration;

        new PixelDataHolder(configuration.tablePrefix());
        new StatisticsLoader();

        if (configuration.enablePlaceholderAPI()) {
            new LobbyPlaceholders<>(configuration.genericModePrefix());
        }

        if (Pixel.LEADER)
            new AutoPurger();
    }

    public T[] enumValues() {
        return configuration.enumType().getEnumConstants();
    }

    public StatsType[] statsValues() {
        return configuration.enumType().getEnumConstants();
    }

}
