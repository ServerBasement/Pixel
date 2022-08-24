package it.mineblock.pixel.match;

public enum SharedMatchStatus {
    OPEN, // pre lobby
    WAITING_LAST, // pre lobby double checking last player joining
    STARTING, // pre lobby -> starting
    CLOSE, // in-game status
    DIED // match finished
}
