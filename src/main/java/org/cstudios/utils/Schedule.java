package org.cstudios.utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.cstudios.CCombatLog;
import org.cstudios.CTeams;

import java.util.concurrent.Executor;

public final class Schedule {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    public static final Executor
       SYNC = (cmd) -> submit(false, cmd),
       ASYNC = (cmd) -> submit(true, cmd);

    public static BukkitTask later(boolean async, BukkitRunnable runnable, long ticks) {
        return
           async
              ? runnable.runTaskLaterAsynchronously(CCombatLog.getInstance(), ticks)
              : runnable.runTaskLater(CCombatLog.getInstance(), ticks);
    }

    public static BukkitTask timer(boolean async, BukkitRunnable runnable, long ticks) {
        return timer(async, runnable, ticks, ticks);
    }

    public static BukkitTask timer(boolean async, BukkitRunnable runnable, long ticks, long delay) {
        return
           async
              ? runnable.runTaskTimerAsynchronously(CCombatLog.getInstance(), ticks, delay)
              : runnable.runTaskTimer(CCombatLog.getInstance(), ticks, delay);
    }

    public static BukkitTask submit(boolean async, BukkitRunnable runnable) {
        return
           async
              ? runnable.runTaskAsynchronously(CCombatLog.getInstance())
              : runnable.runTask(CCombatLog.getInstance());
    }

    public static BukkitTask later(boolean async, Runnable runnable, long ticks) {
        return
           async
              ? SCHEDULER.runTaskLaterAsynchronously(CCombatLog.getInstance(), runnable, ticks)
              : SCHEDULER.runTaskLater(CCombatLog.getInstance(), runnable, ticks);
    }

    public static BukkitTask timer(boolean async, Runnable runnable, long ticks) {
        return timer(async, runnable, ticks, ticks);
    }

    public static BukkitTask timer(boolean async, Runnable runnable, long ticks, long delay) {
        return
           async
              ? SCHEDULER.runTaskTimerAsynchronously(CCombatLog.getInstance(), runnable, ticks, delay)
              : SCHEDULER.runTaskTimer(CCombatLog.getInstance(), runnable, ticks, delay);
    }

    public static BukkitTask submit(boolean async, Runnable runnable) {
        return
           async
              ? SCHEDULER.runTaskAsynchronously(CCombatLog.getInstance(), runnable)
              : SCHEDULER.runTask(CCombatLog.getInstance(), runnable);
    }

}
