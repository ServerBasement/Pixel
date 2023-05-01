package it.ohalee.pixel.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.redisson.api.RCascadeType;
import org.redisson.api.annotation.RCascade;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RObjectField;
import org.redisson.client.codec.LongCodec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Accessors(fluent = true)
@REntity
@RequiredArgsConstructor
public class Statistics {

    @RId
    private final String uuid;
    private final int primaryKey;

    @RCascade(RCascadeType.ALL)
    private final Map<StatsType, Object> data = new ConcurrentHashMap<>();

    @Setter
    private boolean playing;
    @Setter
    private boolean exists;
    @Setter
    @RObjectField(codec = LongCodec.class)
    private long lastTimeSeen;

    public void increment(StatsType key, int amount) {
        data.compute(key, (k, v) -> {
            if (v == null) return amount;
            if (!(v instanceof Number)) return v;
            return ((Number) v).intValue() + amount;
        });
    }

    public void increment(StatsType key) {
        increment(key, 1);
    }

    public void decrement(StatsType key, int amount) {
        increment(key, -amount);
    }

    public void decrement(StatsType key) {
        decrement(key, 1);
    }

    public void write(StatsType key, Object value) {
        data.put(key, value);
    }

    public Object obtain(StatsType key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public @Nullable Object obtain(StatsType key) {
        return obtain(key, null);
    }

    public <V> V obtain(StatsType key, V defaultValue, Class<V> clazz) {
        Object value = obtain(key, defaultValue);
        if (value == null) return null;
        if (!clazz.isAssignableFrom(value.getClass())) return null;
        return clazz.cast(value);
    }

    public int obtainInt(StatsType key) {
        return obtain(key, 0, Integer.class);
    }

    public long obtainLong(StatsType key) {
        return obtain(key, 0L, Long.class);
    }

    public double obtainDouble(StatsType key) {
        return obtain(key, 0D, Double.class);
    }
}
