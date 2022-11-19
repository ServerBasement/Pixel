package it.bowyard.pixel.match;

import it.bowyard.pixel.topics.StatusRequest;
import it.bowyard.pixel.util.Basement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.redisson.api.RMapCache;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RObjectField;
import org.redisson.client.codec.IntegerCodec;

import java.util.concurrent.TimeUnit;

@REntity
@Getter
@Setter
@RequiredArgsConstructor
public abstract class SharedMatch<E extends Enum<E> & PixelType> {

    public SharedMatch() {
        this.name = null;
        this.type = null;
    }

    @RId
    private final String name;
    private final String type;

    private SharedMatchStatus status;
    private String server;

    @RObjectField(codec = IntegerCodec.class)
    private int required, teamSize, teamsNumber, effectivePlayers, joiningPlayers;

    public int totalCount() {
        return (this.getEffectivePlayers() + this.getJoiningPlayers());
    }

    public boolean canCarry(int weight) {
        return !(totalCount()+weight > getRequired());
    }

    public void join(Player player) {
        setJoiningPlayers(getJoiningPlayers()+1);
        if (totalCount() == getRequired()) setStatus(SharedMatchStatus.WAITING_LAST);
        RMapCache<String, String> mapping = Basement.rclient().getMapCache(getName() + "_joining");
        mapping.put(player.getName(), getName());
    }

    public boolean spectate(String player) {
        if (getStatus() != SharedMatchStatus.CLOSE) return false;
        RMapCache<String, String> mapping = Basement.rclient().getMapCache(getServer() + "_spectators");
        mapping.put(player, getName(), 15L, TimeUnit.SECONDS);
        return true;
    }

    private long changedAt = -1;

    public void warranty() {
        if (changedAt == -1) return;
        if (System.currentTimeMillis() > (changedAt+5000))
            Basement.redis().publishMessage(new StatusRequest(getServer(), getName()));
    }

    public abstract Class<E> typeClass();

}
