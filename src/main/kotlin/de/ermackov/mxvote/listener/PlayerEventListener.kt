package de.ermackov.mxvote.listener

import de.ermackov.mxvote.DataProvider
import de.ermackov.mxvote.Voting
import de.ermackov.mxvote.watcher.Watcher
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerEventListener(val plugin: JavaPlugin, data: DataProvider, voting: Voting) : Listener {

    val world: World = Bukkit.getWorlds()[0]
    val watcher: Watcher = Watcher(plugin, data, voting, world)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            checkStartStopWatcher()
        }, 1L) // 1 tick delay
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        // Delay by 1 tick (20 ticks = 1 second)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            checkStartStopWatcher()
        }, 1L) // 1 tick delay
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            checkStartStopWatcher()
        }, 1L) // 1 tick delay
    }

    fun checkStartStopWatcher() {
        val playersInWorld = world.players.size

        if (playersInWorld == 0) {
            watcher.stop()
            return
        }

        if (playersInWorld > 0 && !watcher.isRunning()) {
            watcher.start()
        }
    }
}