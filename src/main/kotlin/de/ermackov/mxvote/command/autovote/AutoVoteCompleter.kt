package de.ermackov.mxvote.command.autovote

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AutoVoteCompleter : TabCompleter {

    // Implement the onTabComplete method
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        // If the sender is a player, proceed
        if (sender is Player) {
            // Check the number of arguments entered
            if (args.size == 1) {
                return listOf("time", "weather", "requests")
            }
            when (args[0]) {
                "time" -> {
                    if (args.size == 2) {
                        return listOf("day", "night", "off")
                    }
                }
                "weather" -> {
                    if (args.size == 2) {
                        return listOf("sunny", "rainy", "off")
                    }
                }
                "requests" -> {
                    if (args.size == 2) {
                        return listOf("yes", "no", "off")
                    }
                }
            }
        }

        return emptyList()  // No suggestions
    }
}

