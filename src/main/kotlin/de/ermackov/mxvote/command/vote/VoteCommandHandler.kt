package de.ermackov.mxvote.command.vote

import de.ermackov.mxvote.Voting
import de.ermackov.mxvote.config.VoteConfig
import de.ermackov.mxvote.entities.Time
import de.ermackov.mxvote.entities.Weather
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class VoteCommandHandler(private val plugin: JavaPlugin, private val config: VoteConfig, val voting: Voting) : CommandExecutor {

    private val prefix: String = ChatColor.translateAlternateColorCodes('&', config.getMessagePrefix())

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!config.isVoteEnabled()){
            sender.sendMessage("$prefix${ChatColor.RED}Voting plugin is disabled!")
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("$prefix${ChatColor.RED}Only players can vote.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$prefix${ChatColor.RED}Please provide an argument.")
            return false
        }

        when (args[0]) {
            "cancel", "c" -> voting.handleCancel(sender)
            "yes", "y" -> voting.handleVote(sender, true)
            "no" -> voting.handleVote(sender, false)
            "day", "d" -> voting.startVote(sender, Time.DAY)
            "night" -> voting.startVote(sender, Time.NIGHT)
            "sunny", "s" -> voting.startVote(sender, Weather.SUNNY)
            "n" -> handleNAlias(sender)
            else -> sender.sendMessage("$prefix${ChatColor.RED}Invalid argument: ${args[0]}")
        }
        return true
    }

    private fun handleNAlias(player: Player) {
        if (voting.voteInProgress){
            voting.handleVote(player, false)
            return
        }
        voting.startVote(player, Time.NIGHT)
    }


}