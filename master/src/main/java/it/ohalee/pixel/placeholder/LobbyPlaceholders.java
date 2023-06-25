package it.ohalee.pixel.placeholder;

import it.ohalee.pixel.PixelProxy;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.player.PixelParticipator;
import it.ohalee.pixel.player.PixelParticipatorManager;
import it.ohalee.pixel.stats.Statistics;
import it.ohalee.pixel.stats.StatsType;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LobbyPlaceholders<E extends Enum<E> & PixelType> extends PlaceholderExpansion {

    private final String prefix;

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "ohAlee";
    }

    @Override
    public String getIdentifier() {
        return "pixel";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (player == null) {
            return "";
        }

        s = s.toUpperCase();

        if (s.startsWith("ONLINE_")) {
            Enum<E> type = E.valueOf(PixelProxy.getRawProxy().getRancher().typeClass(), s.replaceFirst("ONLINE_", ""));
            return String.valueOf(PixelProxy.getRawProxy().getQueue(type).playerLoad());
        }

        PixelParticipator participator = PixelParticipatorManager.get(player.getUniqueId());
        if (participator == null) {
            return "Updating";
        }

        Statistics statistics = participator.statistics();
        if (statistics == null) return "...";

        for (StatsType stat : PixelProxy.getStatistics().statsValues()) {
            if (s.equals(stat.placeholder())) {
                return String.valueOf(statistics.obtain(stat));
            }
        }
        return "Not valid stats_type: " + s;
    }

}
