package de.ermackov.mxvote.command.autovote

import de.ermackov.mxvote.DataProvider
import de.ermackov.mxvote.config.VoteConfig
import de.ermackov.mxvote.entities.Requests
import de.ermackov.mxvote.entities.Time
import de.ermackov.mxvote.entities.Weather
import de.ermackov.mxvote.entities.formatVoteType

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class AutoVoteCommandHandler(private val plugin: JavaPlugin, private val config: VoteConfig, private val data: DataProvider) : CommandExecutor {

    private val prefix: String = ChatColor.translateAlternateColorCodes('&', config.getMessagePrefix())

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!config.isVoteEnabled()) {
            sender.sendMessage("$prefix${ChatColor.RED}Voting plugin is disabled!")
            return true
        }

        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("$prefix${ChatColor.RED}Only players can vote.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(formatAutoVoteData(player))
            return true
        }

        when (args[0].lowercase()) {
            "time" -> handleTimeCommand(player, args)
            "weather" -> handleWeatherCommand(player, args)
            "requests" -> handleRequestsCommand(player, args)
            else -> player.sendMessage("$prefix${ChatColor.RED}Unknown argument: ${args[0]}")
        }

        return true
    }


    private fun handleRequestsCommand(sender: Player, args: Array<out String>) {

        // /autovote requests
        if (args.size <= 1) {
            val current = data.getUserDataByPlayer(sender).autovotes.requests
            sender.sendMessage("$prefix${ChatColor.YELLOW}Your AutoVote Request setting is: ${ChatColor.AQUA}${formatVoteType(current)}")
            sender.sendMessage("$prefix${ChatColor.YELLOW}To change it, use: ${ChatColor.AQUA}/autovote requests <yes|no|off>")
            return
        }

        val newSetting = when (args[1].lowercase()) {
            "yes" -> Requests.YES
            "no"  -> Requests.NO
            "off" -> Requests.OFF
            else -> {
                sender.sendMessage("$prefix${ChatColor.RED}Invalid requests argument: ${args[1]}")
                sender.sendMessage("$prefix${ChatColor.GOLD}Use: /autovote requests <yes|no|off>")
                return
            }
        }

        val user = data.getUserDataByPlayer(sender)
        data.saveUser(user.copy(autovotes = user.autovotes.copy(requests = newSetting)))

        val message = when (newSetting) {
            Requests.YES -> "You will now automatically accept incoming vote requests."
            Requests.NO -> "You will now automatically decline incoming vote requests."
            Requests.OFF -> "You will now manually choose when receiving vote requests."
        }

        sender.sendMessage("$prefix${ChatColor.GREEN}$message")
    }


    private fun handleWeatherCommand(sender: Player, args: Array<out String>) {

        // /autovote weather
        if (args.size <= 1) {
            val current = data.getUserDataByPlayer(sender).autovotes.weather
            sender.sendMessage("$prefix${ChatColor.YELLOW}Your AutoVote Weather setting is: ${ChatColor.AQUA}${formatVoteType(current)}")
            sender.sendMessage("$prefix${ChatColor.YELLOW}To change it, use: ${ChatColor.AQUA}/autovote weather <sunny|rain|thunder|off>")
            return
        }

        val newSetting = when (args[1].lowercase()) {
            "sunny"   -> Weather.SUNNY
            "rain"    -> Weather.RAIN
            "thunder" -> Weather.THUNDER
            "off"     -> Weather.OFF
            else -> {
                sender.sendMessage("$prefix${ChatColor.RED}Invalid weather argument: ${args[1]}")
                sender.sendMessage("$prefix${ChatColor.GOLD}Use: /autovote weather <sunny|rain|thunder|off>")
                return
            }
        }

        val user = data.getUserDataByPlayer(sender)
        data.saveUser(user.copy(autovotes = user.autovotes.copy(weather = newSetting)))

        val message = when (newSetting) {
            Weather.SUNNY -> "You will now automatically vote for clear weather."
            Weather.RAIN -> "You will now automatically vote for rain to stop."
            Weather.THUNDER -> "You will now automatically vote for thunder to stop."
            Weather.OFF -> "You won't automatically vote for weather anymore."
        }

        sender.sendMessage("$prefix${ChatColor.GREEN}$message")
    }


    private fun handleTimeCommand(sender: Player, args: Array<out String>) {

        // /autovote time
        if (args.size <= 1) {
            sender.sendMessage(
                "$prefix${ChatColor.YELLOW}Your current AutoVote Time setting is: " +
                        "${ChatColor.AQUA}${formatVoteType(data.getUserDataByPlayer(sender).autovotes.time)}"
            )
            sender.sendMessage(
                "$prefix${ChatColor.YELLOW}To change it, use: " +
                        "${ChatColor.AQUA}/autovote time <day|night|off>"
            )
            return
        }

        val newSetting = when (args[1].lowercase()) {
            "day"   -> Time.DAY
            "night" -> Time.NIGHT
            "off"   -> Time.OFF
            else -> {
                sender.sendMessage("$prefix${ChatColor.RED}Invalid time argument: ${args[1]}")
                sender.sendMessage("$prefix${ChatColor.GOLD}Use: /autovote time <day|night|off>")
                return
            }
        }

        // Update user entry
        val user = data.getUserDataByPlayer(sender)
        data.saveUser(user.copy(autovotes = user.autovotes.copy(time = newSetting)))

        val message = when (newSetting) {
            Time.DAY ->
                "You will now automatically vote for day when night falls."

            Time.NIGHT ->
                "You will now automatically vote for night when day starts."

            Time.OFF ->
                "You won't automatically vote for time changes anymore."
        }

        sender.sendMessage("$prefix${ChatColor.GREEN}$message")
    }


    private fun formatAutoVoteData(player: Player): String {
        val settings = data.getUserDataByPlayer(player).autovotes

        return buildString {
            append("$prefix${ChatColor.GREEN}Your AutoVote settings:\n")
            append("${ChatColor.YELLOW}Time: ${ChatColor.AQUA}${formatVoteType(settings.time)}\n")
            append("${ChatColor.YELLOW}Weather: ${ChatColor.AQUA}${formatVoteType(settings.weather)}\n")
            append("${ChatColor.YELLOW}Requests: ${ChatColor.AQUA}${formatVoteType(settings.requests)}")
        }
    }

}

