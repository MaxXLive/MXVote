package de.ermackov.mxvote.command.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class InfoTabCompleter : TabCompleter {

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
                return listOf("version", "update", "help", "reload")
            }
        }

        return emptyList()  // No suggestions
    }
}