package it.ohalee.pixel.api;

import it.ohalee.pixel.stats.Statistics;

public interface PixelUser<K> {

    K key();

    void assign(Statistics statistics);

}
