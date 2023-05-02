package it.ohalee.pixel.test;

import it.ohalee.pixel.stats.StatsType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExampleStatsType implements StatsType {

    KILLS(0, "kills", "kills", "Kills", "Kills", true);

    private final int id;
    private final String placeholder;
    private final String dbColumn;
    private final String displayName;
    private final String internalName;
    private final boolean resettable;


    @Override
    public int id() {
        return id;
    }

    @Override
    public String placeholder() {
        return placeholder;
    }

    @Override
    public String dbColumn() {
        return dbColumn;
    }

    @Override
    public String internalName() {
        return internalName;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public boolean resettable() {
        return resettable;
    }

}
