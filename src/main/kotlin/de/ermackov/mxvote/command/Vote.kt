package de.ermackov.mxvote.command

import de.ermackov.mxvote.config.VoteConfig
import de.ermackov.mxvote.entities.Vote
import de.ermackov.mxvote.entities.VoteType
import de.ermackov.mxvote.entities.formatVoteType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil

class Vote(private val plugin: JavaPlugin, private val config: VoteConfig) : CommandExecutor {

    private val prefix: String = ChatColor.translateAlternateColorCodes('&', config.getMessagePrefix())

    private var votes = mutableSetOf<Vote>()
    private var type: VoteType = VoteType.NONE
    private var voteInProgress = false
    private var world: World? = null
    private var timeLeft = config.getVoteDuration()
    private var bossBar: BossBar? = null


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
            sender.sendMessage("$prefix${ChatColor.RED}Please provide a argument.")
            return false
        }

        when (args[0]){
            "status" -> handleStatus(sender)
            "cancel" -> handleCancel(sender)
            "yes" -> handleVote(sender, true)
            "no" -> handleVote(sender, false)
            "day" -> startVote(sender, VoteType.DAY)
            "night" -> startVote(sender, VoteType.NIGHT)
            "sunny" -> startVote(sender, VoteType.SUNNY)
            else -> sender.sendMessage("$prefix${ChatColor.RED}Invalid argument: ${args[0]}")
        }
        return true
    }

    private fun handleStatus(player: Player) {
        if (!voteInProgress){
            player.sendMessage("$prefix There is no vote currently running.")
            return
        }

        player.sendMessage("---------- Current voting ----------")
        player.sendMessage("Initialed by: ${getVoteInitiator()?.name}")
        player.sendMessage("Voting for: ${formatVoteType(type)}")
        player.sendMessage("Time left: $timeLeft seconds")
        player.sendMessage("----------------------------------")
    }

    private fun handleCancel(player: Player) {
        if(getVoteInitiator() != player){
            player.sendMessage("$prefix${ChatColor.RED}You are not the initiator of this voting!")
            return
        }

        Bukkit.broadcastMessage("$prefix${ChatColor.YELLOW}${player.name} has canceled the voting for: ${formatVoteType(type)}")
        resetVoting()
    }

    private fun startVote(initiator: Player, voteType: VoteType) {
        if( (!initiator.hasPermission("mxvote.vote.day")) && voteType == VoteType.DAY){
            initiator.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return
        }
        if( (!initiator.hasPermission("mxvote.vote.night")) && voteType == VoteType.NIGHT){
            initiator.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return
        }
        if( (!initiator.hasPermission("mxvote.vote.sunny")) && voteType == VoteType.SUNNY){
            initiator.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return
        }
        if (voteInProgress) {
            initiator.sendMessage("$prefix${ChatColor.YELLOW}A voting is already in progress. Click below to vote:")
            sendVoteOptions(initiator)
            return
        }

        votes.clear()
        voteInProgress = true
        type = voteType
        world = initiator.world
        timeLeft = config.getVoteDuration()

        votes.add(Vote(initiator, isInitiator = true, voteYes = true))

        Bukkit.broadcastMessage("$prefix${ChatColor.YELLOW}${initiator.name} has started a voting for: ${formatVoteType(type)}")
        broadcastVote(initiator)

        createBossBar()
        // Schedule vote timeout (e.g., 30 seconds)
        object : BukkitRunnable() {
            override fun run() {
                if (!voteInProgress) {
                    cancel()
                    return
                }
                timeLeft--
                updateBossBar()
                if (timeLeft <= 0) {
                    endVote()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 20L, 20L)

        checkResults()
    }


    private fun broadcastVote(playerToExclude: Player) {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player != playerToExclude) {
                sendVoteOptions(player)
            }
        }
    }

    private fun sendVoteOptions(player: Player) {
        player.sendMessage("${ChatColor.AQUA}Click below to vote:")

        val message = TextComponent("${ChatColor.GREEN}[YES] ")
        message.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes")

        val messageNo = TextComponent("${ChatColor.RED}[NO]")
        messageNo.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no")

        val fullMessage = ComponentBuilder()
            .append(message)
            .append(" ")
            .append(messageNo)
            .create()

        player.spigot().sendMessage(*fullMessage)
    }

    private fun handleVote(player: Player, voteYes: Boolean) {
        if(!player.hasPermission("mxvote.vote.contribute")){
            player.sendMessage("$prefix${ChatColor.RED}You don't have permission to use this command!")
            return
        }

        if (!canPlayerVote(player)) {
            player.sendMessage("$prefix${ChatColor.RED}You have already voted!")
            return
        }

        votes.add(Vote(player, false, voteYes))
        player.sendMessage("$prefix${ChatColor.AQUA}You have voted: ${ if (voteYes) "Yes" else "No"}")
        checkResults()
    }

    private fun requiredVotes(): Int {
        return ceil(Bukkit.getOnlinePlayers().size * 0.51).toInt()
    }

    private fun applyTime(time: Long) {
        Bukkit.broadcastMessage("$prefix${ChatColor.AQUA}Setting time to ${formatVoteType(type)}")
        startTimeTransition(world!!, time)
    }

    private fun startTimeTransition(world: World, targetTime: Long) {
        val stepSize = config.getTimeChangeStepSize()
        var newTime = world.time

        plugin.logger.info("target time: $targetTime")
        plugin.logger.info("target time range : [${targetTime - stepSize}, ${targetTime + stepSize}]")
        object : BukkitRunnable() {
            override fun run() {
                // Update time by step
                newTime += stepSize

                plugin.logger.info("current time: $newTime")

                // If the time exceeds the target time, stop the task
                if (newTime >= targetTime - stepSize && newTime <= targetTime + stepSize) {
                    world.time = targetTime
                    cancel()
                    return
                }

                if( newTime >= 24000){
                    newTime = 0
                }

                world.time = newTime
            }
        }.runTaskTimer(plugin, 0L, 1L) // 1L is the delay between each step
    }

    private fun applyWeather(){
        world?.setStorm(false)
        world?.isThundering = false
        Bukkit.broadcastMessage("$prefix${ChatColor.AQUA}Setting weather to clear.")
    }

    private fun checkResults(){
        if (countYes() >= requiredVotes()) {
            voteSuccessful()
        }

        if (votes.size >= Bukkit.getOnlinePlayers().size){
            endVote()
        }
    }

    private fun endVote() {
        if (countYes() >= votes.count()) {
            voteSuccessful()
        }else{
            voteUnsuccessful()
        }
    }

    private fun voteUnsuccessful() {
        Bukkit.broadcastMessage("$prefix${ChatColor.RED}Voting failed. (${countYes()} of ${votes.size} voters agreed (${(countYes().toDouble() / votes.size.toDouble()) * 100}%))\")")
        resetVoting()
    }

    private fun voteSuccessful() {
        Bukkit.broadcastMessage("$prefix${ChatColor.AQUA}Voting passed! (${countYes()} of ${votes.size} voters agreed (${(countYes().toDouble()/votes.size.toDouble())*100}%))")
        when (type){
            VoteType.DAY -> applyTime(1000)
            VoteType.NIGHT -> applyTime(18000)
            VoteType.SUNNY -> applyWeather()
            VoteType.NONE -> plugin.logger.warning("$prefix Vote was successful but type is NONE!")
        }
        resetVoting()
    }

    private fun resetVoting() {
        voteInProgress = false
        votes.clear()
        type = VoteType.NONE
        world = null
        bossBar?.isVisible = false
    }

    private fun canPlayerVote(player: Player): Boolean {
        for (vote in votes) {
            if (vote.player == player){
                return false
            }
        }
        return true
    }

    private fun getVoteInitiator(): Player? {
        for (vote in votes) {
            if (vote.isInitiator){
                return vote.player
            }
        }
        return null
    }

    private fun countYes(): Int{
        var i = 0
        for (vote in votes) {
            if (vote.voteYes){
                i++
            }
        }
        return i
    }

    private fun createBossBar() {
        val bossBar = Bukkit.createBossBar(
            "${ChatColor.YELLOW}Voting started for: ${formatVoteType(type)}}",
            BarColor.YELLOW,
            BarStyle.SOLID
        )
        bossBar.isVisible = true

        for (player in Bukkit.getOnlinePlayers()){
            bossBar.addPlayer(player)
        }
        this.bossBar = bossBar
    }

    // Update the BossBar with progress
    private fun updateBossBar() {
        if (bossBar == null){
            return
        }

        val progress = 1.0 - (timeLeft / config.getVoteDuration().toDouble()) // Calculate the progress (0.0 to 1.0)
        bossBar?.progress = progress

        // Optionally, change color based on progress
        when {
            progress > 0.7 -> bossBar?.color = BarColor.RED
            progress > 0.3 -> bossBar?.color = BarColor.YELLOW
            else -> bossBar?.color = BarColor.GREEN
        }

        // Update the BossBar title dynamically
        bossBar?.setTitle("${ChatColor.AQUA}Voting for ${formatVoteType(type)} | Ends in $timeLeft s")
    }
}