package it.ohalee.pixel.data.leaderboard;

import it.ohalee.basementlib.api.persistence.maria.structure.data.QueryData;
import it.ohalee.pixel.stats.StatsType;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.*;

public class LeaderBoard {

    public static final long TOMORROW = 86400000L;
    public static final long NEXT_WEEK = 604800000L;
    public static final long NEXT_MONTH = 2592000000L;

    @Getter
    @Accessors(fluent = true)
    public enum Type {
        DAILY("Daily", TOMORROW, 1),
        WEEKLY("Weekly", NEXT_WEEK, 2),
        MONTHLY("Monthly", NEXT_MONTH, 3),
        ALLTIME("Lifetime", null, 0);

        private final String displayName;
        private final Long time;
        private final int id;

        Type(String displayName, Long time, int id) {
            this.displayName = displayName;
            this.time = time;
            this.id = id;
        }

    }

    private HashMap<String, Integer> leaderboard = new HashMap<>();

    public LeaderBoard(QueryData data, StatsType top) {
        while (data.next()) {
            String username = data.getString("username");
            String uuid = data.getString("uuid");
            int value = data.getInt(top.dbColumn());
            // TODO: 29/04/2023 configurable
            leaderboard.put(username /*+ TagAdapter.getColorSuffix(UUID.fromString(uuid))*/, value);
        }
    }

    public LeaderBoard() {
        leaderboard = null;
    }

    public String submit(int position) {
        LinkedList<Map.Entry<String, Integer>> ordered = orderedBoard();
        if (position > ordered.size()) return "Nessuno: §a0"; // TODO: 29/04/2023 configurable
        Map.Entry<String, Integer> entry = ordered.get(position - 1);
        return entry.getKey() + " §a" + entry.getValue();
    }

    private LinkedList<Map.Entry<String, Integer>> orderedBoard() {
        LinkedList<Map.Entry<String, Integer>> list = new LinkedList<>(leaderboard.entrySet());
        list.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));
        return list;
    }

}
