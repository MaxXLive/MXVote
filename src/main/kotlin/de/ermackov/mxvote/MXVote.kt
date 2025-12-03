package de.ermackov.mxvote

import de.ermackov.mxvote.command.autovote.AutoVoteCompleter
import de.ermackov.mxvote.command.autovote.AutoVoteCommandHandler
import de.ermackov.mxvote.command.info.InfoCommandHandler
import de.ermackov.mxvote.command.info.InfoTabCompleter
import de.ermackov.mxvote.command.vote.VoteCommandHandler
import de.ermackov.mxvote.command.vote.VoteTabCompleter

import de.ermackov.mxvote.config.VoteConfig
import de.ermackov.mxvote.listener.PlayerEventListener
import org.bukkit.plugin.java.JavaPlugin

class MXVote : JavaPlugin() {

    private lateinit var voting: Voting
    private lateinit var voteCommandHandler: VoteCommandHandler
    private lateinit var infoCommandHandler: InfoCommandHandler
    private lateinit var autoVoteCommandHandler: AutoVoteCommandHandler
    private lateinit var config: VoteConfig
    private lateinit var data: DataProvider
    private lateinit var playerEventListener: PlayerEventListener

    override fun onEnable() {
        logger.info("MXVote Enabled")
        config = VoteConfig(this)
        data = DataProvider(this)
        voting = Voting(this, config, data)
        voteCommandHandler = VoteCommandHandler(this, config, voting)
        infoCommandHandler = InfoCommandHandler(this, config)
        autoVoteCommandHandler = AutoVoteCommandHandler(this, config, data)
        playerEventListener = PlayerEventListener(this, data, voting)

        getCommand("mxvote")?.setExecutor(infoCommandHandler)
        getCommand("mxvote")?.tabCompleter = InfoTabCompleter();

        getCommand("vote")?.setExecutor(voteCommandHandler)
        getCommand("vote")?.tabCompleter = VoteTabCompleter();

        getCommand("autovote")?.setExecutor(autoVoteCommandHandler)
        getCommand("autovote")?.tabCompleter = AutoVoteCompleter();

        server.pluginManager.registerEvents(playerEventListener, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
