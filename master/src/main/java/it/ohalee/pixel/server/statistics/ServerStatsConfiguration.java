package it.ohalee.pixel.server.statistics;

import it.ohalee.pixel.stats.StatsType;

public abstract class ServerStatsConfiguration<C extends Enum<?> & StatsType> {

    public abstract Class<C> enumType();

    public abstract String genericModePrefix();

    public abstract String tablePrefix();

    public abstract boolean enablePlaceholderAPI();

}
