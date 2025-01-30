package de.ermackov.mxvote.command

import de.ermackov.mxvote.config.VoteConfig
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Test(private val plugin: JavaPlugin, config: VoteConfig): CommandExecutor {

    private val prefix: String = ChatColor.translateAlternateColorCodes('&', config.getMessagePrefix())

    override fun onCommand(sender: CommandSender, command: Command, arg: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("mxvote.test.version")){
            sender.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return true
        }

        sender.sendMessage("{$plugin.description.name} (${plugin.description.version})")
        return true
    }
}