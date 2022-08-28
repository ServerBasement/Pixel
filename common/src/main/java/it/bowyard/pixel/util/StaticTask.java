package it.bowyard.pixel.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class StaticTask {

    private static JavaPlugin main;
    public StaticTask(JavaPlugin main) {
        StaticTask.main = main;
    }

    public static void cancelAll() {
        Bukkit.getScheduler().cancelTasks(main);
    }

    public static BukkitTask runBukkitTask(BukkitRunnable task, boolean async) {
        if (!async)
            return task.runTask(main);
        return task.runTaskAsynchronously(main);
    }

    public static BukkitTask runBukkitTaskTimer(BukkitRunnable task, long delay, long period, boolean async) {
        if (!async)
            return task.runTaskTimer(main, delay, period);
        return task.runTaskTimerAsynchronously(main, delay, period);
    }

    public static BukkitTask runBukkitTaskLater(BukkitRunnable task , long delay, boolean async) {
        if (!async)
            return task.runTaskLater(main, delay);
        return task.runTaskLaterAsynchronously(main, delay);
    }

    public static BukkitTask runTask(Runnable runnable, boolean async) {
        if (!async)return Bukkit.getScheduler().runTask(main, runnable);
        return Bukkit.getScheduler().runTaskAsynchronously(main, runnable);
    }

    public static BukkitTask runTaskLater(Runnable runnable, long delay, boolean async) {
        if (!async) return Bukkit.getScheduler().runTaskLater(StaticTask.main, runnable, delay);
        return Bukkit.getScheduler().runTaskLaterAsynchronously(StaticTask.main, runnable, delay);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long delay, long period, boolean async) {
        if (!async) return Bukkit.getScheduler().runTaskTimer(StaticTask.main, runnable, delay, period);
        return Bukkit.getScheduler().runTaskTimerAsynchronously(StaticTask.main, runnable, delay, period);
    }

}
