package it.ohalee.pixel.data;

import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.ohalee.basementlib.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.ohalee.basementlib.api.persistence.maria.structure.AbstractMariaDatabase;
import it.ohalee.pixel.util.Basement;

public class PixelDataHolder {

    public static QueryBuilderSelect DEFAULT_SELECT_USER;
    public static QueryBuilderSelect DEFAULT_SELECT_ID;
    public static QueryBuilderSelect DEFAULT_SELECT_UUID;
    public static QueryBuilderDelete DELETE_USER;

    public PixelDataHolder(String prefix) {
        AbstractMariaDatabase database = Basement.get().database();

        DEFAULT_SELECT_USER = database.select().columns("*").from(prefix + "_users").limit(4, 0);
        DEFAULT_SELECT_UUID = database.select().columns("uuid").from("players").limit(1, 0);
        DEFAULT_SELECT_ID = database.select().columns("id").from("players").limit(1, 0);
        DELETE_USER = database.delete().multiTable(prefix + "_users").multiFrom(prefix + "_users", "players");
    }

}
