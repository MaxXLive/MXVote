package de.ermackov.mxvote.command.vote

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class VoteTabCompleter : TabCompleter {

    // Implement the onTabComplete method
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        // If the sender is a player, proceed
        if (sender is Player) {
            // Check the number of arguments entered
            if (args.size == 1) {
                // Suggest vote types (e.g., DAY, NIGHT, WEATHER)
                return listOf("day", "night", "sunny")
            }
        }

        return emptyList()  // No suggestions
    }
}