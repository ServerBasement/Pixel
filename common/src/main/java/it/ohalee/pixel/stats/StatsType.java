package it.ohalee.pixel.stats;

public interface StatsType {

    int id();

    String placeholder();

    String dbColumn();

    String internalName();

    String displayName();

    boolean resettable();

}
