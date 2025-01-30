package de.ermackov.mxvote.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object Vote: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, arg: String, args: Array<out String>): Boolean {

        var str = ""
        for (arg in args) {
            str += "$arg, "
        }

        sender.sendMessage("This is a test: $command, $arg, $str")
        return true
    }
}