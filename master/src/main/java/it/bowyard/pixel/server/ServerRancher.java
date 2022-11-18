package it.bowyard.pixel.server;

import it.bowyard.pixel.Pixel;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.server.handler.MasterSwitchHandler;
import it.bowyard.pixel.server.handler.MasterSwitchMessage;
import it.bowyard.pixel.util.Basement;
import it.bowyard.pixel.util.StaticTask;
import it.hemerald.basementx.api.bukkit.events.BasementNewServerFound;
import it.hemerald.basementx.api.bukkit.events.BasementServerRemoved;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RSetCache;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Setter
public class ServerRancher<E extends Enum<E> & PixelType, T extends SharedMatch<E>> implements Listener {

    protected final ServerRancherConfiguration<E, T> configuration;

    private final String modeName;
    protected int MAX_MATCHES_PER_SERVER;
    protected double WARNING_PERCENTAGE;

    private List<Integer> available_indexes;

    protected final Map<String, InternalServer<E, T>> internalServers = new HashMap<>();

    private final RSetCache<String> lobbies;

    public MasterSwitchMessage unload() {
        lobbies.remove(Basement.get().getServerID());
        if (!Pixel.LEADER) return null;
        Pixel.setLEADER(false);
        Iterator<String> iterator = lobbies.iterator(1);
        if (!iterator.hasNext())
            return null;
        return new MasterSwitchMessage(configuration.modeName(), iterator.next());
    }

    public ServerRancher(JavaPlugin plugin, ServerRancherConfiguration<E, T> configuration) {
        Basement.redis().registerTopicListener(MasterSwitchMessage.TOPIC, new MasterSwitchHandler(configuration.modeName()));
        lobbies = Basement.rclient().getSetCache(configuration.modeName() + "_lobbies");
        lobbies.add(Basement.get().getServerID());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.configuration = configuration;
        available_indexes = IntStream.range(1, configuration.maxAmountOfServers()).boxed().collect(Collectors.toList());
        this.modeName = configuration.modeName();
        this.MAX_MATCHES_PER_SERVER = configuration.maxMatchesPerServer();
        this.WARNING_PERCENTAGE = configuration.warningPercentage();
    }

    public void start() {
        Basement.get().getServerManager().getOnlineServers(modeName + "_instance_")
                .forEach(server -> {
                    int index = Integer.parseInt(server.getName().split("_")[2]);
                    available_indexes.remove(Integer.valueOf(index));
                    internalServers.put(server.getName(), configuration.internalSupplier(index, server, internalServers.size() > configuration.minimumIdle(), configuration.sharedMatchClass()));
                    Pixel.LOGGER.info("Found " + server.getName() + " server loaded at index " + index);
                });
        if (Pixel.LEADER) {
            if (internalServers.size() < configuration.minimumIdle()) {
                startServer(Math.abs(internalServers.size()-configuration.minimumIdle()));
            }
        }
        StaticTask.runBukkitTaskTimer(new DangerTask(this), 20L*3, 20L*3, true);
    }

    protected void startServer(int many) {
        for (int i = 0; i < many; i++) {
            if (available_indexes.isEmpty()) return;
            Integer index = available_indexes.remove(0);
            Basement.get().getRemoteCerebrumService().createServer(modeName + "_instance_" + index);
        }
    }

    @EventHandler
    protected void serverFound(BasementNewServerFound serverEvent) {
        if (serverEvent.getServer().getName().startsWith(modeName + "_instance_")) {
            int index = Integer.parseInt(serverEvent.getServer().getName().split("_")[2]);
            InternalServer<E, T> server = configuration.internalSupplier(index, serverEvent.getServer(), internalServers.size() > configuration.minimumIdle(), configuration.sharedMatchClass());
            internalServers.put(serverEvent.getServer().getName(), server);
            Pixel.LOGGER.info("Registered Server -> " + serverEvent.getServer().getName());
        }
    }

    @EventHandler
    protected void serverRemoved(BasementServerRemoved serverEvent) {
        if (serverEvent.getServer().getName().startsWith(modeName + "_instance_")) {
            InternalServer<E, T> server = internalServers.remove(serverEvent.getServer().getName());
            server.destroy();
            available_indexes.add(server.getIndex());
            System.out.println("Removed Server -> " + serverEvent.getServer().getName());
        }
    }

    public InternalServer<E, T> getServer(String name) {
        return internalServers.get(name);
    }

    public int getRunningMatches() {
        return internalServers.values().stream().mapToInt(InternalServer::size).sum();
    }

    public int getRunningServers() {
        return internalServers.size();
    }

    public Optional<InternalServer<E, T>> seekServer() {
        return internalServers.values().stream()
                .filter(server -> server.size() != MAX_MATCHES_PER_SERVER && server.isSeekable())
                .min(Comparator.comparingInt(InternalServer::size));
    }

    public Optional<InternalServer<E, T>> get(String name) {
        return Optional.ofNullable(internalServers.get(name));
    }

}
