package de.ermackov.mxvote.watcher

import de.ermackov.mxvote.DataProvider
import de.ermackov.mxvote.Voting
import de.ermackov.mxvote.entities.Time
import de.ermackov.mxvote.entities.VoteType
import de.ermackov.mxvote.entities.Weather
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class Watcher(private val plugin: JavaPlugin, private val data: DataProvider, private val voting: Voting, private val world: World) {

    private var taskId: Int = -1
    private var cooldown: Int = 1 * 60  // 10 minutes cooldown in seconds

    fun isRunning(): Boolean {
        return taskId != -1
    }

    fun start() {
        if (taskId != -1) return // already running

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            timerTick()
        }, 20L, 20L) // 20 ticks = 1 second
    }

    private fun checkAutoVotes(currentTime: Time, currentWeather: Weather, reasonToVote: VoteType) {

        val playersToVote = mutableMapOf<Player, VoteType>()

        data.getUserData().users.forEach { user ->
            val player = Bukkit.getPlayer(UUID.fromString(user.id)) ?: return@forEach
            if (player.world != world) return@forEach

            when (reasonToVote) {
                is Time -> {
                    // Only vote if user wants this time and it's different from the current time
                    if (user.autovotes.time != reasonToVote && user.autovotes.time != Time.OFF) {
                        playersToVote[player] = user.autovotes.time
                    }
                }

                is Weather -> {
                    // Only vote if user wants this weather and it's different from the current weather
                    if (user.autovotes.weather != reasonToVote && user.autovotes.weather != Weather.OFF) {
                        playersToVote[player] = user.autovotes.weather
                    }
                }

                else -> {} // Handle other VoteType in future if needed
            }
        }


        if (playersToVote.isNotEmpty()) {
            println("Triggering auto-vote for $reasonToVote in world ${world.name}")
            voting.startGroupVote(playersToVote.keys.toList(), world, playersToVote.values.first())
        }
    }



    fun stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = -1
        }
    }

    fun getDayTime(): Time {
        val time = world.time
        return if (time in 12001..23500) Time.NIGHT else Time.DAY
    }

    fun getCurrentWeather(): Weather = when {
        world.isThundering -> Weather.THUNDER
        world.hasStorm() -> Weather.RAIN
        world.isClearWeather -> Weather.SUNNY
        else -> Weather.OFF
    }


    var lastDayTime = Time.OFF
    var lastWeather = Weather.OFF

    open fun timerTick() {
//        println("current time: ${getDayTime()}, last time: $lastDayTime")
//        println("current weather: ${getCurrentWeather()}, last weather: $lastWeather")

        val updates = listOf(
            getDayTime() to lastDayTime,
            getCurrentWeather() to lastWeather
        )

        updates.forEach { (current, last) ->
            if (current != last) {
                checkAutoVotes(getDayTime(), getCurrentWeather(), current)
            }
        }

        lastDayTime = getDayTime()
        lastWeather = getCurrentWeather()
    }

}
