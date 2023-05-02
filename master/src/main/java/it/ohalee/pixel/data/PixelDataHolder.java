package it.ohalee.pixel.data;

import it.ohalee.basementlib.api.persistence.maria.queries.builders.WhereBuilder;
import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.ohalee.basementlib.api.persistence.maria.structure.AbstractMariaDatabase;
import it.ohalee.pixel.data.leaderboard.LeaderBoard;
import it.ohalee.pixel.stats.StatsType;
import it.ohalee.pixel.util.Basement;

import java.util.concurrent.CompletableFuture;

public class PixelDataHolder {

    public static QueryBuilderSelect DEFAULT_SELECT_USER;
    public static QueryBuilderSelect DEFAULT_SELECT_ID;
    public static QueryBuilderSelect DEFAULT_SELECT_UUID;
    public static QueryBuilderDelete DELETE_USER;

    public static QueryBuilderSelect GENERIC_LEADERBOARD_SELECT;
    public static QueryBuilderUpdate LEADERBOARD_UPDATE;

    public PixelDataHolder(String prefix) {
        AbstractMariaDatabase database = Basement.get().database();

        DEFAULT_SELECT_USER = database.select().columns("*").from(prefix + "_users").limit(4, 0);
        DEFAULT_SELECT_UUID = database.select().columns("uuid").from("players").limit(1, 0);
        DEFAULT_SELECT_ID = database.select().columns("id").from("players").limit(1, 0);
        DELETE_USER = database.delete().multiTable(prefix + "_users").multiFrom(prefix + "_users", "players");

        GENERIC_LEADERBOARD_SELECT = database.select().from(prefix + "_leaderboard", "players");
        LEADERBOARD_UPDATE = database.update().table(prefix + "_leaderboard");
    }

    public static CompletableFuture<LeaderBoard> leaderBoard(StatsType top, LeaderBoard.Type type) {
        return GENERIC_LEADERBOARD_SELECT.patternClone()
                .columns("username", "uuid", top.dbColumn())
                .orderBy(top.dbColumn() + " DESC")
                .limit(10, 0)
                .where(WhereBuilder.builder().equalsNQ("players.id", "player_id").and().equals("temporal", type.id()).close())
                .build()
                .execReturnAsync().thenApplyAsync(queryData -> new LeaderBoard(queryData, top));
    }
}
